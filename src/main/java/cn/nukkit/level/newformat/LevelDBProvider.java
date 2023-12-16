package cn.nukkit.level.newformat;

import cn.nukkit.Server;
import cn.nukkit.api.UsedByReflection;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.GameType;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.LevelException;
import cn.nukkit.utils.SemVersion;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class LevelDBProvider implements LevelProvider {
    private static final byte[] levelDatMagic = new byte[]{10, 0, 0, 0, 68, 11, 0, 0};
    private final ThreadLocal<WeakReference<Chunk>> lastChunk = new ThreadLocal<>();
    protected final ConcurrentMap<Long, Chunk> chunks = new ConcurrentHashMap<>();
    protected final Path path;
    protected final LevelDat levelDat;
    protected final Level level;
    private final DB db;

    public LevelDBProvider(Level level, String path) throws IOException {
        this(level, path, new Options()
                .createIfMissing(true)
                .compressionType(CompressionType.ZLIB_RAW)
                .blockSize(64 * 1024));
    }

    public LevelDBProvider(Level level, String path, Options options) throws IOException {
        this.level = level;
        this.path = Path.of(path);
        File filePath = this.path.toFile();
        if (!filePath.exists() && !filePath.mkdirs()) {
            throw new LevelException("Could not create the directory " + filePath);
        }

        var levelDat = readLevelDat();
        if (levelDat == null) {
            levelDat = LevelDat.builder().build();
            this.levelDat = levelDat;
            saveLevelData();
        } else {
            this.levelDat = levelDat;
        }

        File dbFolder = this.path.resolve("db").toFile();
        try {
            if (!dbFolder.exists()) dbFolder.mkdirs();
            db = net.daporkchop.ldbjni.LevelDB.PROVIDER.open(dbFolder, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @UsedByReflection
    public static boolean isValid(String path) {
        boolean isValid = (new File(path + "/level.dat").exists()) && new File(path + "/db/").isDirectory();
        if (isValid) {
            for (File file : Objects.requireNonNull(new File(path + "/db/").listFiles())) {
                if (!file.getName().endsWith(".ldb") && !file.getName().endsWith(".log") && !file.getName().equals("CURRENT") && !file.getName().startsWith("MANIFEST-")) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    public Chunk loadChunk(long index, int chunkX, int chunkZ, boolean create) {
        IChunk chunk;
        try {
            chunk = region.readChunk(chunkX - regionX * 32, chunkZ - regionZ * 32);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (chunk == null) {
            if (create) {
                chunk = this.getEmptyChunk(chunkX, chunkZ);
                putChunk(index, chunk);
            }
        } else {
            putChunk(index, chunk);
        }
        return chunk;
    }

    public CompletableFuture<Chunk> readChunk(int x, int z, DimensionInfo dimensionInfo) throws WorldStorageException {
        return CompletableFuture.supplyAsync(() -> {
            AllayUnsafeChunk.Builder builder = AllayUnsafeChunk.builder()
                    .chunkX(x)
                    .chunkZ(z)
                    .dimensionInfo(dimensionInfo);
            byte[] versionValue = this.db.get(LevelDBKeyUtils.VERSION.getKey(x, z, dimensionInfo));
            if (versionValue == null) {
                versionValue = this.db.get(LevelDBKeyUtils.LEGACY_VERSION.getKey(x, z, dimensionInfo));
            }
            if (versionValue == null) {
                return builder.build().toSafeChunk();
            }
            byte[] finalized = this.db.get(LevelDBKeyUtils.CHUNK_FINALIZED_STATE.getKey(x, z, dimensionInfo));
            if (finalized == null) {
                builder.state(ChunkState.FINISHED);
            } else {
                builder.state(ChunkState.values()[Unpooled.wrappedBuffer(finalized).readIntLE() + 1]);
            }
            LevelDBChunkSerializer.INSTANCE.deserialize(this.db, builder);
            return builder.build().toSafeChunk();
        }, Server.getInstance().getVirtualThreadPool());
    }

    public int size() {
        return this.chunks.size();
    }

    @Override
    public void unloadChunks() {
        var iter = chunks.values().iterator();
        while (iter.hasNext()) {
            iter.next().unload(true, false);
            iter.remove();
        }
    }

    @Override
    public String getGenerator() {
        return this.levelDat.getGeneratorName();
    }

    @Override
    public Map<String, Object> getGeneratorOptions() {
        return new HashMap<String, Object>() {
            {
                put("preset", LevelDBProvider.this.levelDat.getGeneratorOptions());
            }
        };
    }

    @Override
    public Map<Long, IChunk> getLoadedChunks() {
        return ImmutableMap.copyOf(chunks);
    }

    @Override
    public void doGarbageCollection() {

    }

    @Override
    public boolean isChunkLoaded(int X, int Z) {
        return isChunkLoaded(Level.chunkHash(X, Z));
    }

    public void putChunk(long index, Chunk chunk) {
        chunks.put(index, chunk);
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        return this.chunks.containsKey(hash);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, IChunk chunk) {

    }

    @Override
    public DimensionData getDimensionData() {
        return level.getDimensionData();
    }

    @Override
    public AsyncTask requestChunkTask(int X, int Z) {
        return null;
    }

    @Override
    public String getPath() {
        return path.toString();
    }

    public Server getServer() {
        return this.level.getServer();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public String getName() {
        return this.levelData.getString("LevelName");
    }

    @Override
    public boolean isRaining() {
        return this.levelData.getBoolean("raining");
    }

    @Override
    public void setRaining(boolean raining) {
        this.levelData.putBoolean("raining", raining);
    }

    @Override
    public int getRainTime() {
        return this.levelData.getInt("rainTime");
    }

    @Override
    public void setRainTime(int rainTime) {
        this.levelData.putInt("rainTime", rainTime);
    }

    @Override
    public boolean isThundering() {
        return this.levelData.getBoolean("thundering");
    }

    @Override
    public void setThundering(boolean thundering) {
        this.levelData.putBoolean("thundering", thundering);
    }

    @Override
    public int getThunderTime() {
        return this.levelData.getInt("thunderTime");
    }

    @Override
    public void setThunderTime(int thunderTime) {
        this.levelData.putInt("thunderTime", thunderTime);
    }

    @Override
    public long getCurrentTick() {
        return this.levelData.getLong("Time");
    }

    @Override
    public void setCurrentTick(long currentTick) {
        this.levelData.putLong("Time", currentTick);
    }

    @Override
    public long getTime() {
        return this.levelData.getLong("DayTime");
    }

    @Override
    public void setTime(long value) {
        this.levelData.putLong("DayTime", value);
    }

    @Override
    public long getSeed() {
        return this.levelData.getLong("RandomSeed");
    }

    @Override
    public void setSeed(long value) {
        this.levelData.putLong("RandomSeed", value);
    }

    @Override
    public Vector3 getSpawn() {
        return this.levelDat.getSpawnPoint().asVector3();
    }

    @Override
    public void setSpawn(Vector3 pos) {
        this.levelDat.setSpawnPoint(new BlockVector3((int) pos.x, (int) pos.y, (int) pos.z));
    }

    @Override
    public GameRules getGamerules() {
        return this.levelDat.getGameRules();
    }

    @Override
    public void setGameRules(GameRules rules) {
        this.levelDat.setGameRules(rules);
    }

    @Override
    public void saveChunks() {
        for (Chunk chunk : this.chunks.values()) {
            if (chunk.getBlockChanges() != 0) {
                chunk.setChanged(false);
                this.saveChunk(chunk.getX(), chunk.getZ());
            }
        }
    }

    @Override
    public void saveChunk(int X, int Z) {

    }

    @Override
    public void saveChunk(int X, int Z, IChunk chunk) {

    }

    public LevelDat getLevelDat() {
        return this.levelDat;
    }

    @Override
    public void saveLevelData() {
        File levelDat = path.resolve("level.dat").toFile();
        if (!levelDat.exists()) {
            try {
                levelDat.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (var output = new FileOutputStream(levelDat)) {
            if (levelDat.exists()) {
                Files.copy(path.resolve("level.dat"), path.resolve("level.dat_old"), StandardCopyOption.REPLACE_EXISTING);
            }
            output.write(levelDatMagic);//magic number
            NBTIO.write(createWorldDataNBT(this.levelDat), output, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateLevelName(String name) {
        if (!this.getName().equals(name)) {
            this.levelData.putString("LevelName", name);
        }
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ) {
        return this.loadChunk(chunkX, chunkZ, false);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index)) {
            return true;
        }
        return loadChunk(index, chunkX, chunkZ, create) != null;
    }

    @Override
    public boolean unloadChunk(int X, int Z) {
        return this.unloadChunk(X, Z, true);
    }

    @Override
    public boolean unloadChunk(int X, int Z, boolean safe) {
        long index = Level.chunkHash(X, Z);
        BaseFullChunk chunk = this.chunks.get(index);
        if (chunk != null && chunk.unload(false, safe)) {
            lastChunk.set(null);
            this.chunks.remove(index, chunk);
            return true;
        }
        return false;
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Nullable
    protected final BaseFullChunk getThreadLastChunk() {
        var ref = lastChunk.get();
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    @Override
    public Chunk getLoadedChunk(int chunkX, int chunkZ) {
        var tmp = getThreadLastChunk();
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        lastChunk.set(new WeakReference<>(tmp = chunks.get(index)));
        return tmp;
    }

    @Override
    public Chunk getLoadedChunk(long hash) {
        var tmp = getThreadLastChunk();
        if (tmp != null && tmp.getIndex() == hash) {
            return tmp;
        }
        lastChunk.set(new WeakReference<>(tmp = chunks.get(hash)));
        return tmp;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, boolean create) {
        var tmp = getThreadLastChunk();
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        lastChunk.set(new WeakReference<>(tmp = chunks.get(index)));
        if (tmp != null) {
            return tmp;
        } else {
            tmp = this.loadChunk(index, chunkX, chunkZ, create);
            lastChunk.set(new WeakReference<>(tmp));
            return tmp;
        }
    }

    @Override
    public IChunk getEmptyChunk(int x, int z) {
        return null;
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof BaseFullChunk)) {
            throw new ChunkException("Invalid Chunk class");
        }
        chunk.setProvider(this);
        chunk.setPosition(chunkX, chunkZ);
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index) && !this.chunks.get(index).equals(chunk)) {
            this.unloadChunk(chunkX, chunkZ, false);
        }
        this.chunks.put(index, (BaseFullChunk) chunk);
    }

    @Override
    public boolean isChunkPopulated(int chunkX, int chunkZ) {
        BaseFullChunk chunk = this.getChunk(chunkX, chunkZ);
        return chunk != null && chunk.isPopulated();
    }

    @Override
    public synchronized void close() {
        this.unloadChunks();
    }

    @Override
    public boolean isChunkGenerated(int chunkX, int chunkZ) {
        return true;
    }

    public synchronized LevelDat readLevelDat() throws IOException {
        File levelDat = path.resolve("level.dat").toFile();
        if (!levelDat.exists()) return null;
        try (var input = new FileInputStream(levelDat)) {
            //The first 8 bytes are magic number
            input.skip(8);
            BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream(input.readAllBytes()));
            CompoundTag d = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
            stream.close();
            CompoundTag abilities = d.getCompound("abilities");
            CompoundTag experiments = d.getCompound("experiments");
            GameRules gameRules = GameRules.getDefault();
            gameRules.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, d.getBoolean("bonusChestSpawned"));
            gameRules.setGameRule(GameRule.COMMAND_BLOCKS_ENABLED, d.getBoolean("commandblocksenabled"));
            gameRules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, d.getBoolean("dodaylightcycle"));
            gameRules.setGameRule(GameRule.DO_ENTITY_DROPS, d.getBoolean("doentitydrops"));
            gameRules.setGameRule(GameRule.DO_FIRE_TICK, d.getBoolean("dofiretick"));
            gameRules.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, d.getBoolean("doimmediaterespawn"));
            gameRules.setGameRule(GameRule.DO_INSOMNIA, d.getBoolean("doinsomnia"));
            gameRules.setGameRule(GameRule.DO_LIMITED_CRAFTING, d.getBoolean("dolimitedcrafting"));
            gameRules.setGameRule(GameRule.DO_MOB_LOOT, d.getBoolean("domobloot"));
            gameRules.setGameRule(GameRule.DO_MOB_SPAWNING, d.getBoolean("domobspawning"));
            gameRules.setGameRule(GameRule.DO_TILE_DROPS, d.getBoolean("dotiledrops"));
            gameRules.setGameRule(GameRule.DO_WEATHER_CYCLE, d.getBoolean("doweathercycle"));
            gameRules.setGameRule(GameRule.DROWNING_DAMAGE, d.getBoolean("drowningdamage"));
            gameRules.setGameRule(GameRule.FALL_DAMAGE, d.getBoolean("falldamage"));
            gameRules.setGameRule(GameRule.FIRE_DAMAGE, d.getBoolean("firedamage"));
            gameRules.setGameRule(GameRule.FREEZE_DAMAGE, d.getBoolean("freezedamage"));
            gameRules.setGameRule(GameRule.FUNCTION_COMMAND_LIMIT, d.getInt("functioncommandlimit"));
            gameRules.setGameRule(GameRule.KEEP_INVENTORY, d.getBoolean("keepinventory"));
            gameRules.setGameRule(GameRule.MAX_COMMAND_CHAIN_LENGTH, d.getInt("maxcommandchainlength"));
            gameRules.setGameRule(GameRule.MOB_GRIEFING, d.getBoolean("mobgriefing"));
            gameRules.setGameRule(GameRule.NATURAL_REGENERATION, d.getBoolean("naturalregeneration"));
            gameRules.setGameRule(GameRule.PVP, d.getBoolean("pvp"));
            gameRules.setGameRule(GameRule.RESPAWN_BLOCKS_EXPLODE, d.getBoolean("respawnblocksexplode"));
            gameRules.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, d.getBoolean("sendcommandfeedback"));
            gameRules.setGameRule(GameRule.SHOW_BORDER_EFFECT, d.getBoolean("showbordereffect"));
            gameRules.setGameRule(GameRule.SHOW_COORDINATES, d.getBoolean("showcoordinates"));
            gameRules.setGameRule(GameRule.SHOW_DEATH_MESSAGES, d.getBoolean("showdeathmessages"));
            gameRules.setGameRule(GameRule.SHOW_TAGS, d.getBoolean("showtags"));
            gameRules.setGameRule(GameRule.SPAWN_RADIUS, d.getInt("spawnradius"));
            gameRules.setGameRule(GameRule.TNT_EXPLODES, d.getBoolean("tntexplodes"));
            return LevelDat.builder()
                    .biomeOverride(d.getString("BiomeOverride"))
                    .centerMapsToOrigin(d.getBoolean("CenterMapsToOrigin"))
                    .confirmedPlatformLockedContent(d.getBoolean("ConfirmedPlatformLockedContent"))
                    .difficulty(d.getInt("Difficulty"))
                    .flatWorldLayers(d.getString("FlatWorldLayers"))
                    .forceGameType(d.getBoolean("ForceGameType"))
                    .gameType(GameType.from(d.getInt("GameType")))
                    .generator(d.getInt("Generator"))
                    .inventoryVersion(d.getString("InventoryVersion"))
                    .LANBroadcast(d.getBoolean("LANBroadcast"))
                    .LANBroadcastIntent(d.getBoolean("LANBroadcastIntent"))
                    .lastPlayed(d.getLong("LastPlayed"))
                    .name(d.getString("LevelName"))
                    .limitedWorldOriginPoint(new BlockVector3(d.getInt("LimitedWorldOriginX"), d.getInt("LimitedWorldOriginY"), d.getInt("LimitedWorldOriginZ")))
                    .minimumCompatibleClientVersion(SemVersion.from(d.getIntArray("MinimumCompatibleClientVersion")))
                    .multiplayerGame(d.getBoolean("MultiplayerGame"))
                    .multiplayerGameIntent(d.getBoolean("MultiplayerGameIntent"))
                    .netherScale(d.getInt("NetherScale"))
                    .networkVersion(d.getInt("NetworkVersion"))
                    .platform(d.getInt("Platform"))
                    .platformBroadcastIntent(d.getInt("PlatformBroadcastIntent"))
                    .randomSeed(d.getLong("RandomSeed"))
                    .spawnV1Villagers(d.getBoolean("SpawnV1Villagers"))
                    .spawnPoint(new BlockVector3(d.getInt("SpawnX"), d.getInt("SpawnY"), d.getInt("SpawnZ")))
                    .storageVersion(d.getInt("StorageVersion"))
                    .time(d.getLong("Time"))
                    .worldVersion(d.getInt("WorldVersion"))
                    .XBLBroadcastIntent(d.getInt("XBLBroadcastIntent"))
                    .abilities(LevelDat.Abilities.builder()
                            .attackMobs(abilities.getBoolean("attackmobs"))
                            .attackPlayers(abilities.getBoolean("attackplayers"))
                            .build(abilities.getBoolean("build"))
                            .doorsAndSwitches(abilities.getBoolean("doorsandswitches"))
                            .flySpeed(abilities.getFloat("flySpeed"))
                            .flying(abilities.getBoolean("flying"))
                            .instaBuild(abilities.getBoolean("instabuild"))
                            .invulnerable(abilities.getBoolean("invulnerable"))
                            .lightning(abilities.getBoolean("lightning"))
                            .mayFly(abilities.getBoolean("mayfly"))
                            .mine(abilities.getBoolean("mine"))
                            .op(abilities.getBoolean("op"))
                            .openContainers(abilities.getBoolean("opencontainers"))
                            .teleport(abilities.getBoolean("teleport"))
                            .walkSpeed(abilities.getFloat("walkSpeed"))
                            .build())
                    .baseGameVersion(d.getString("baseGameVersion"))
                    .bonusChestEnabled(d.getBoolean("bonusChestEnabled"))
                    .bonusChestSpawned(d.getBoolean("bonusChestSpawned"))
                    .cheatsEnabled(d.getBoolean("cheatsEnabled"))
                    .commandsEnabled(d.getBoolean("commandsEnabled"))
                    .currentTick(d.getLong("currentTick"))
                    .daylightCycle(d.getInt("daylightCycle"))
                    .editorWorldType(d.getInt("editorWorldType"))
                    .eduOffer(d.getInt("eduOffer"))
                    .educationFeaturesEnabled(d.getBoolean("educationFeaturesEnabled"))
                    .experiments(LevelDat.Experiments.builder()
                            .cameras(experiments.getBoolean("cameras"))
                            .dataDrivenBiomes(experiments.getBoolean("data_driven_biomes"))
                            .dataDrivenItems(experiments.getBoolean("data_driven_items"))
                            .experimentalMolangFeatures(experiments.getBoolean("experimental_molang_features"))
                            .experimentsEverUsed(experiments.getBoolean("experiments_ever_used"))
                            .savedWithToggledExperiments(experiments.getBoolean("saved_with_toggled_experiments"))
                            .upcomingCreatorFeatures(experiments.getBoolean("upcoming_creator_features"))
                            .villagerTradesRebalance(experiments.getBoolean("villager_trades_rebalance"))
                            .build())
                    .hasBeenLoadedInCreative(d.getBoolean("hasBeenLoadedInCreative"))
                    .hasLockedBehaviorPack(d.getBoolean("hasLockedBehaviorPack"))
                    .hasLockedResourcePack(d.getBoolean("hasLockedResourcePack"))
                    .immutableWorld(d.getBoolean("immutableWorld"))
                    .isCreatedInEditor(d.getBoolean("isCreatedInEditor"))
                    .isExportedFromEditor(d.getBoolean("isExportedFromEditor"))
                    .isFromLockedTemplate(d.getBoolean("isFromLockedTemplate"))
                    .isFromWorldTemplate(d.getBoolean("isFromWorldTemplate"))
                    .isRandomSeedAllowed(d.getBoolean("isRandomSeedAllowed"))
                    .isSingleUseWorld(d.getBoolean("isSingleUseWorld"))
                    .isWorldTemplateOptionLocked(d.getBoolean("isWorldTemplateOptionLocked"))
                    .lastOpenedWithVersion(SemVersion.from(d.getIntArray("lastOpenedWithVersion")))
                    .lightningLevel(d.getFloat("lightningLevel"))
                    .lightningTime(d.getInt("lightningTime"))
                    .limitedWorldDepth(d.getInt("limitedWorldDepth"))
                    .limitedWorldWidth(d.getInt("limitedWorldWidth"))
                    .permissionsLevel(d.getInt("permissionsLevel"))
                    .playerPermissionsLevel(d.getInt("playerPermissionsLevel"))
                    .playersSleepingPercentage(d.getInt("playerssleepingpercentage"))
                    .prid(d.getString("prid"))
                    .rainLevel(d.getFloat("rainLevel"))
                    .rainTime(d.getInt("rainTime"))
                    .randomTickSpeed(d.getInt("randomtickspeed"))
                    .recipesUnlock(d.getBoolean("recipesunlock"))
                    .requiresCopiedPackRemovalCheck(d.getBoolean("requiresCopiedPackRemovalCheck"))
                    .serverChunkTickRange(d.getInt("serverChunkTickRange"))
                    .spawnMobs(d.getBoolean("spawnMobs"))
                    .startWithMapEnabled(d.getBoolean("startWithMapEnabled"))
                    .texturePacksRequired(d.getBoolean("texturePacksRequired"))
                    .useMsaGamertagsOnly(d.getBoolean("useMsaGamertagsOnly"))
                    .worldStartCount(d.getLong("worldStartCount"))
                    .worldPolicies(LevelDat.WorldPolicies.builder().build())
                    .build();
        } catch (FileNotFoundException e) {
            log.error("The level.dat file does not exist!");
        }
        throw new RuntimeException("level.dat is null!");
    }

    private CompoundTag createWorldDataNBT(LevelDat worldData) {
        CompoundTag levelDat = new CompoundTag();

        levelDat.putString("BiomeOverride", worldData.getBiomeOverride());
        levelDat.putBoolean("CenterMapsToOrigin", worldData.isCenterMapsToOrigin());
        levelDat.putBoolean("ConfirmedPlatformLockedContent", worldData.isConfirmedPlatformLockedContent());
        levelDat.putInt("Difficulty", worldData.getDifficulty());
        levelDat.putString("FlatWorldLayers", worldData.getFlatWorldLayers());
        levelDat.putBoolean("ForceGameType", worldData.isForceGameType());
        levelDat.putInt("GameType", worldData.getGameType().ordinal());
        levelDat.putInt("Generator", worldData.getGenerator());
        levelDat.putString("InventoryVersion", worldData.getInventoryVersion());
        levelDat.putBoolean("LANBroadcast", worldData.isLANBroadcast());
        levelDat.putBoolean("LANBroadcastIntent", worldData.isLANBroadcastIntent());
        levelDat.putLong("LastPlayed", worldData.getLastPlayed());
        levelDat.putString("LevelName", worldData.getName());
        levelDat.putInt("LimitedWorldOriginX", worldData.getLimitedWorldOriginPoint().getX());
        levelDat.putInt("LimitedWorldOriginY", worldData.getLimitedWorldOriginPoint().getY());
        levelDat.putInt("LimitedWorldOriginZ", worldData.getLimitedWorldOriginPoint().getZ());
        levelDat.putIntArray("MinimumCompatibleClientVersion", worldData.getMinimumCompatibleClientVersion().toArray());
        levelDat.putBoolean("MultiplayerGame", worldData.isMultiplayerGame());
        levelDat.putBoolean("MultiplayerGameIntent", worldData.isMultiplayerGameIntent());
        levelDat.putInt("NetherScale", worldData.getNetherScale());
        levelDat.putInt("NetworkVersion", worldData.getNetworkVersion());
        levelDat.putInt("Platform", worldData.getPlatform());
        levelDat.putInt("PlatformBroadcastIntent", worldData.getPlatformBroadcastIntent());
        levelDat.putLong("RandomSeed", worldData.getRandomSeed());
        levelDat.putBoolean("SpawnV1Villagers", worldData.isSpawnV1Villagers());
        levelDat.putInt("SpawnX", worldData.getSpawnPoint().getX());
        levelDat.putInt("SpawnY", worldData.getSpawnPoint().getY());
        levelDat.putInt("SpawnZ", worldData.getSpawnPoint().getZ());
        levelDat.putInt("StorageVersion", worldData.getStorageVersion());
        levelDat.putLong("Time", worldData.getTime());
        levelDat.putInt("WorldVersion", worldData.getWorldVersion());
        levelDat.putInt("XBLBroadcastIntent", worldData.getXBLBroadcastIntent());
        CompoundTag abilities = new CompoundTag()
                .putBoolean("attackmobs", worldData.getAbilities().isAttackMobs())
                .putBoolean("attackplayers", worldData.getAbilities().isAttackPlayers())
                .putBoolean("build", worldData.getAbilities().isBuild())
                .putBoolean("doorsandswitches", worldData.getAbilities().isDoorsAndSwitches())
                .putBoolean("flying", worldData.getAbilities().isFlying())
                .putBoolean("instabuild", worldData.getAbilities().isInstaBuild())
                .putBoolean("invulnerable", worldData.getAbilities().isInvulnerable())
                .putBoolean("lightning", worldData.getAbilities().isLightning())
                .putBoolean("mayfly", worldData.getAbilities().isMayFly())
                .putBoolean("mine", worldData.getAbilities().isMine())
                .putBoolean("op", worldData.getAbilities().isOp())
                .putBoolean("opencontainers", worldData.getAbilities().isOpenContainers())
                .putBoolean("teleport", worldData.getAbilities().isTeleport())
                .putFloat("flySpeed", worldData.getAbilities().getFlySpeed())
                .putFloat("walkSpeed", worldData.getAbilities().getWalkSpeed());
        CompoundTag experiments = new CompoundTag()
                .putBoolean("cameras", worldData.getExperiments().isCameras())
                .putBoolean("data_driven_biomes", worldData.getExperiments().isDataDrivenBiomes())
                .putBoolean("data_driven_items", worldData.getExperiments().isDataDrivenItems())
                .putBoolean("experimental_molang_features", worldData.getExperiments().isExperimentalMolangFeatures())
                .putBoolean("experiments_ever_used", worldData.getExperiments().isExperimentsEverUsed())
                .putBoolean("gametest", worldData.getExperiments().isGametest())
                .putBoolean("saved_with_toggled_experiments", worldData.getExperiments().isSavedWithToggledExperiments())
                .putBoolean("upcoming_creator_features", worldData.getExperiments().isUpcomingCreatorFeatures())
                .putBoolean("villager_trades_rebalance", worldData.getExperiments().isVillagerTradesRebalance());
        levelDat.put("abilities", abilities);
        levelDat.put("experiments", experiments);

        levelDat.putBoolean("bonusChestEnabled", worldData.isBonusChestEnabled());
        levelDat.putBoolean("bonusChestSpawned", worldData.isBonusChestSpawned());
        levelDat.putBoolean("cheatsEnabled", worldData.isCheatsEnabled());
        levelDat.putBoolean("commandsEnabled", worldData.isCommandsEnabled());
        levelDat.putLong("currentTick", worldData.getCurrentTick());
        levelDat.putInt("daylightCycle", worldData.getDaylightCycle());
        levelDat.putInt("editorWorldType", worldData.getEditorWorldType());
        levelDat.putInt("eduOffer", worldData.getEduOffer());
        levelDat.putBoolean("educationFeaturesEnabled", worldData.isEducationFeaturesEnabled());

        levelDat.put("commandblockoutput", worldData.getGameRules().getGameRules().get(GameRule.COMMAND_BLOCK_OUTPUT).getTag());
        levelDat.put("commandblocksenabled", worldData.getGameRules().getGameRules().get(GameRule.COMMAND_BLOCKS_ENABLED).getTag());
        levelDat.put("dodaylightcycle", worldData.getGameRules().getGameRules().get(GameRule.DO_DAYLIGHT_CYCLE).getTag());
        levelDat.put("doentitydrops", worldData.getGameRules().getGameRules().get(GameRule.DO_ENTITY_DROPS).getTag());
        levelDat.put("dofiretick", worldData.getGameRules().getGameRules().get(GameRule.DO_FIRE_TICK).getTag());
        levelDat.put("doimmediaterespawn", worldData.getGameRules().getGameRules().get(GameRule.DO_IMMEDIATE_RESPAWN).getTag());
        levelDat.put("doinsomnia", worldData.getGameRules().getGameRules().get(GameRule.DO_INSOMNIA).getTag());
        levelDat.put("dolimitedcrafting", worldData.getGameRules().getGameRules().get(GameRule.DO_LIMITED_CRAFTING).getTag());
        levelDat.put("domobloot", worldData.getGameRules().getGameRules().get(GameRule.DO_MOB_LOOT).getTag());
        levelDat.put("domobspawning", worldData.getGameRules().getGameRules().get(GameRule.DO_MOB_SPAWNING).getTag());
        levelDat.put("dotiledrops", worldData.getGameRules().getGameRules().get(GameRule.DO_TILE_DROPS).getTag());
        levelDat.put("doweathercycle", worldData.getGameRules().getGameRules().get(GameRule.DO_WEATHER_CYCLE).getTag());
        levelDat.put("drowningdamage", worldData.getGameRules().getGameRules().get(GameRule.DROWNING_DAMAGE).getTag());
        levelDat.put("falldamage", worldData.getGameRules().getGameRules().get(GameRule.FALL_DAMAGE).getTag());
        levelDat.put("firedamage", worldData.getGameRules().getGameRules().get(GameRule.FIRE_DAMAGE).getTag());
        levelDat.put("freezedamage", worldData.getGameRules().getGameRules().get(GameRule.FREEZE_DAMAGE).getTag());
        levelDat.put("functioncommandlimit", worldData.getGameRules().getGameRules().get(GameRule.FUNCTION_COMMAND_LIMIT).getTag());
        levelDat.put("keepinventory", worldData.getGameRules().getGameRules().get(GameRule.KEEP_INVENTORY).getTag());
        levelDat.put("maxcommandchainlength", worldData.getGameRules().getGameRules().get(GameRule.MAX_COMMAND_CHAIN_LENGTH).getTag());
        levelDat.put("mobgriefing", worldData.getGameRules().getGameRules().get(GameRule.MOB_GRIEFING).getTag());
        levelDat.put("naturalregeneration", worldData.getGameRules().getGameRules().get(GameRule.NATURAL_REGENERATION).getTag());
        levelDat.put("pvp", worldData.getGameRules().getGameRules().get(GameRule.PVP).getTag());
        levelDat.put("respawnblocksexplode", worldData.getGameRules().getGameRules().get(GameRule.RESPAWN_BLOCKS_EXPLODE).getTag());
        levelDat.put("sendcommandfeedback", worldData.getGameRules().getGameRules().get(GameRule.SEND_COMMAND_FEEDBACK).getTag());
        levelDat.put("showbordereffect", worldData.getGameRules().getGameRules().get(GameRule.SHOW_BORDER_EFFECT).getTag());
        levelDat.put("showcoordinates", worldData.getGameRules().getGameRules().get(GameRule.SHOW_COORDINATES).getTag());
        levelDat.put("showdeathmessages", worldData.getGameRules().getGameRules().get(GameRule.SHOW_DEATH_MESSAGES).getTag());
        levelDat.put("showtags", worldData.getGameRules().getGameRules().get(GameRule.SHOW_TAGS).getTag());
        levelDat.put("spawnradius", worldData.getGameRules().getGameRules().get(GameRule.SPAWN_RADIUS).getTag());
        levelDat.put("tntexplodes", worldData.getGameRules().getGameRules().get(GameRule.TNT_EXPLODES).getTag());
        return levelDat;
    }
}
