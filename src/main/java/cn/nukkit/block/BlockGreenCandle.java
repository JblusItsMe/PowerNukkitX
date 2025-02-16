package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGreenCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockGreenCandleCake();
    }
}