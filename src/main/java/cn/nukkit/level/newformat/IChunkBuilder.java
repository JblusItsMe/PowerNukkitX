package cn.nukkit.level.newformat;

import cn.nukkit.level.DimensionData;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.List;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public interface IChunkBuilder {
    IChunkBuilder chunkX(int chunkX);

    int getChunkX();

    IChunkBuilder chunkZ(int chunkZ);

    int getChunkZ();

    IChunkBuilder state(ChunkState state);

    IChunkBuilder levelProvider(LevelProvider levelProvider);

    DimensionData getDimensionData();

    IChunkBuilder sections(ChunkSection[] sections);

    ChunkSection[] getSections();

    IChunkBuilder heightMap(short[] heightMap);

    IChunkBuilder entities(List<CompoundTag> entities);

    IChunkBuilder blockEntities(List<CompoundTag> blockEntities);

    IChunk build();

    IChunk emptyChunk(int chunkX, int chunkZ);
}
