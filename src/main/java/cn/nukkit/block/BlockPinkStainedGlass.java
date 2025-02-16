package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockPinkStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_STAINED_GLASS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}