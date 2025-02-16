package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockBlackStainedGlass extends BlockGlassStained {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}