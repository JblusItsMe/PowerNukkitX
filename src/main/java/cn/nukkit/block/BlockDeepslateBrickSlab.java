package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateBrickSlab(BlockState blockstate) {
        super(blockstate, DEEPSLATE_BRICK_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Deepslate Brick";
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

}