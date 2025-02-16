package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockYellowCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockYellowCandleCake();
    }
}