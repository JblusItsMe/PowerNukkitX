package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonRoots extends BlockRoots implements BlockFlowerPot.FlowerPotBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_ROOTS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonRoots(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crimson Roots";
    }
}