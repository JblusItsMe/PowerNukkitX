package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockCyanWool extends BlockWool {
    public static final BlockProperties PROPERTIES = new BlockProperties(CYAN_WOOL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}