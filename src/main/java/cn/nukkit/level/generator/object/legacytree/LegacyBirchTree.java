package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSource;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LegacyBirchTree extends LegacyTreeGenerator {
    @Override
    public WoodType getType() {
        return WoodType.BIRCH;
    }

    @Override
    public void placeObject(BlockManager level, int x, int y, int z, RandomSource random) {
        this.treeHeight = random.nextInt(2) + 5;
        super.placeObject(level, x, y, z, random);
    }
}
