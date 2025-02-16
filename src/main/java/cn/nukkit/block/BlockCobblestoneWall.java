package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCobblestoneWall extends BlockWallBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLESTONE_WALL, CommonBlockProperties.WALL_BLOCK_TYPE, CommonBlockProperties.WALL_CONNECTION_TYPE_EAST, CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH, CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH, CommonBlockProperties.WALL_CONNECTION_TYPE_WEST, CommonBlockProperties.WALL_POST_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobblestoneWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobblestoneWall(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cobblestone Wall";
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}