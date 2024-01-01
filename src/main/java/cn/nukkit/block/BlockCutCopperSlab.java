package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCutCopperSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }
}