package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGreenGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}