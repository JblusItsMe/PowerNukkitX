package cn.nukkit.registry;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.OK;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BiomeRegistry extends BaseRegistry<Integer, BiomeRegistry.BiomeDefinition, BiomeRegistry.BiomeDefinition> {
    private static final Int2ObjectOpenHashMap<BiomeDefinition> DEFINITIONS = new Int2ObjectOpenHashMap<>(0xFF);
    private static final Object2IntOpenHashMap<String> NAME2ID = new Object2IntOpenHashMap<>(0xFF);
    private static final List<CompoundTag> REGISTRY = new ArrayList<>(0xFF);


    @Override
    public void init() {
        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("biome_id_and_type.json")) {
            Gson gson = new Gson();
            Map<String, ?> map = gson.fromJson(new InputStreamReader(stream), Map.class);
            for (var e : map.entrySet()) {
                NAME2ID.put(e.getKey(), Integer.parseInt(((Map<String, ?>) e.getValue()).get("id").toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("biome_definitions.nbt")) {
            CompoundTag compoundTag = NBTIO.readCompressed(stream);
            Map<String, Tag> tags = compoundTag.getTags();

            for (var e : tags.entrySet()) {
                int id = NAME2ID.getInt(e.getKey());
                CompoundTag value = (CompoundTag) e.getValue();
                ListTag<StringTag> tags1 = value.getList("tags", StringTag.class);
                List<String> list = tags1.getAll().stream().map(StringTag::parseValue).toList();
                BiomeDefinition biomeDefinition = new BiomeDefinition(
                        value.getFloat("ash"),
                        value.getFloat("blue_spores"),
                        value.getFloat("depth"),
                        value.getFloat("downfall"),
                        value.getFloat("height"),
                        value.getString("name_hash"),
                        (byte) value.getByte("rain"),
                        value.getFloat("red_spores"),
                        list,
                        value.getFloat("temperature"),
                        value.getFloat("waterColorA"),
                        value.getFloat("waterColorB"),
                        value.getFloat("waterColorG"),
                        value.getFloat("waterColorR"),
                        value.getFloat("waterTransparency"),
                        value.getFloat("white_ash")
                );
                register(id, biomeDefinition);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BiomeDefinition get(Integer key) {
        return DEFINITIONS.get(key);
    }

    public int getBiomeId(String biomeName) {
        return NAME2ID.getInt(biomeName);
    }

    public byte[] getBiomeDefinitionListPacketData() {
        CompoundTag compoundTag = new CompoundTag();
        for (var r : REGISTRY) {
            compoundTag.putCompound(r.getString("name_hash"), r);
        }
        try {
            return NBTIO.writeNetwork(compoundTag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void trim() {
        DEFINITIONS.trim();
        NAME2ID.trim();
    }

    @Override
    public OK<?> register(Integer key, BiomeDefinition value) {
        if (DEFINITIONS.putIfAbsent(key, value) == null) {
            NAME2ID.put(value.name_hash, key);
            REGISTRY.add(value.toNBT());
            return OK.TRUE;
        } else {
            return new OK<>(false, new IllegalArgumentException("This biome has already been registered with the id: " + key));
        }
    }

    public record BiomeDefinition(float ash,
                                  float blue_spores,
                                  float depth,
                                  float downfall,
                                  float height,
                                  String name_hash,
                                  byte rain,
                                  float red_spores,
                                  List<String> tags,
                                  float temperature,
                                  float waterColorA,
                                  float waterColorB,
                                  float waterColorG,
                                  float waterColorR,
                                  float waterTransparency,
                                  float white_ash) {

        public CompoundTag toNBT() {
            ListTag<StringTag> stringTagListTag = new ListTag<>();
            for (var s : tags) {
                stringTagListTag.add(new StringTag("", s));
            }
            return new CompoundTag()
                    .putFloat("ash", ash)
                    .putFloat("blue_spores", blue_spores)
                    .putFloat("depth", depth)
                    .putFloat("downfall", downfall)
                    .putFloat("height", height)
                    .putString("name_hash", name_hash)
                    .putByte("rain", rain)
                    .putFloat("red_spores", red_spores)
                    .putList("tags", stringTagListTag)
                    .putFloat("temperature", temperature)
                    .putFloat("waterColorA", waterColorA)
                    .putFloat("waterColorB", waterColorB)
                    .putFloat("waterColorG", waterColorG)
                    .putFloat("waterColorG", waterColorG)
                    .putFloat("waterColorR", waterColorR)
                    .putFloat("waterTransparency", waterTransparency)
                    .putFloat("white_ash", white_ash);
        }
    }
}
