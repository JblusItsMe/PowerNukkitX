package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.BlockID.WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;

public class BlockWaxedWeatheredCutCopperSlab extends BlockWeatheredCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCutCopperSlab(BlockState blockstate) {
        super(blockstate,WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB);
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}