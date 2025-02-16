package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement99 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_99");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement99() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement99(BlockState blockstate) {
        super(blockstate);
    }
}