package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.NewLeafType;
import cn.nukkit.block.property.enums.OldLeafType;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSource;

public abstract class LegacyTreeGenerator {
    protected int treeHeight = 7;

    public static void growTree(BlockManager level, int x, int y, int z, RandomSource random, WoodType type, boolean tall) {
        LegacyTreeGenerator tree;
        switch (type) {
            case SPRUCE:
                tree = new LegacySpruceTree();
                break;
            case BIRCH:
                if (tall) {
                    tree = new LegacyTallBirchTree();
                } else {
                    tree = new LegacyBirchTree();
                }
                break;
            case JUNGLE:
                tree = new LegacyJungleTree();
                break;
            case OAK:
            default:
                tree = new LegacyOakTree();
                //todo: more complex treeeeeeeeeeeeeeeee
                break;
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, x, y, z, random);
        }
    }

    protected boolean overridable(String id) {
        return switch (id) {
            case Block.AIR, Block.SAPLING, Block.WOOD, Block.LEAVES, Block.SNOW_LAYER, Block.LEAVES2 -> true;
            default -> false;
        };
    }

    public WoodType getType() {
        return WoodType.OAK;
    }

    public String getLeafBlock() {
        return Block.LEAVES;
    }

    public int getTreeHeight() {
        return treeHeight;
    }

    public boolean canPlaceObject(BlockManager level, int x, int y, int z, RandomSource random) {
        int radiusToCheck = 0;
        for (int yy = 0; yy < this.getTreeHeight() + 3; ++yy) {
            if (yy == 1 || yy == this.getTreeHeight()) {
                ++radiusToCheck;
            }
            for (int xx = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int zz = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.overridable(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void placeObject(BlockManager level, int x, int y, int z, RandomSource random) {

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

        for (int yy = y - 3 + this.getTreeHeight(); yy <= y + this.getTreeHeight(); ++yy) {
            double yOff = yy - (y + this.getTreeHeight());
            int mid = (int) (1 - yOff / 2);
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextInt(2) == 0)) {
                        continue;
                    }
                    Block blockAt = level.getBlockAt(xx, yy, zz);
                    if (!blockAt.isSolid()) {
                        level.setBlockStateAt(xx, yy, zz, getLeafBlockState());
                    }
                }
            }
        }
    }

    protected void placeTrunk(BlockManager level, int x, int y, int z, RandomSource random, int trunkHeight) {
        // The base dirt block
        level.setBlockAt(x, y - 1, z, Block.get(Block.DIRT));

        for (int yy = 0; yy < trunkHeight; ++yy) {
            String blockId = level.getBlockIdAt(x, y + yy, z);
            if (this.overridable(blockId)) {
                level.setBlockStateAt(x, y + yy, z, getTrunkBlockState());
            }
        }
    }

    protected BlockState getTrunkBlockState() {
        return BlockWood.PROPERTIES.getBlockState(CommonBlockProperties.WOOD_TYPE, getType());
    }

    protected BlockState getLeafBlockState() {
        boolean newLeaf;
        switch (getType()) {
            case ACACIA, DARK_OAK -> newLeaf = true;
            default -> newLeaf = false;
        }
        if (newLeaf) {
            return BlockLeaves2.PROPERTIES.getBlockState(CommonBlockProperties.NEW_LEAF_TYPE, NewLeafType.valueOf(getType().name().toUpperCase()));
        } else {
            return BlockLeaves.PROPERTIES.getBlockState(CommonBlockProperties.OLD_LEAF_TYPE, OldLeafType.valueOf(getType().name().toUpperCase()));
        }
    }
}
