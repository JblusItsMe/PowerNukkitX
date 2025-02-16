package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPLE_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockPurpleCandle();
    }
}