package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_STAINED_GLASS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}