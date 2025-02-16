package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockBambooFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:bamboo_fence");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooFence(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Bamboo Fence";
    }
}