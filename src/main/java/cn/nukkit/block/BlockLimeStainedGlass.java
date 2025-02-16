package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockLimeStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIME_STAINED_GLASS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}