package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockBlackstone extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blackstone");

    public BlockBlackstone() {
        super(PROPERTIES.getDefaultState());
    }

    protected BlockBlackstone(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Blackstone";
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 6;
    }
}
