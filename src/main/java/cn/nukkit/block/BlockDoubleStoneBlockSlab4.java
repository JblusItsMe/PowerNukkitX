package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType4;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleStoneBlockSlab4 extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(DOUBLE_STONE_BLOCK_SLAB4, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_4);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleStoneBlockSlab4() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleStoneBlockSlab4(BlockState blockstate) {
        super(blockstate);
    }

    public StoneSlabType4 getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4);
    }

    public void setSlabType(StoneSlabType4 type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4, type);
    }

    @Override
    public String getSlabName() {
        return getSlabType().name();
    }

    @Override
    public double getResistance() {
        return getToolType() > ItemTool.TIER_WOODEN ? 30 : 15;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getSingleSlabId() {
        return STONE_BLOCK_SLAB4;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}