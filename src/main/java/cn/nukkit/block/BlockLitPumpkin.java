package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockLitPumpkin extends BlockPumpkin {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_PUMPKIN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitPumpkin() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitPumpkin(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jack o'Lantern";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        return false;
    }
}