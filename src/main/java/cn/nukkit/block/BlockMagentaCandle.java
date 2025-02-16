package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGENTA_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockMagentaCandleCake();
    }
}