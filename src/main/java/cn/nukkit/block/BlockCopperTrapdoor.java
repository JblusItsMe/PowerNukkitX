package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCopperTrapdoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}