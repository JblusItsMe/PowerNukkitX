package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorAcacia;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:acacia_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Acacia Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorAcacia();
    }
}