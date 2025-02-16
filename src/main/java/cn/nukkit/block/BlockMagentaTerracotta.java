package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaTerracotta extends BlockHardenedClay {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGENTA_TERRACOTTA);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}