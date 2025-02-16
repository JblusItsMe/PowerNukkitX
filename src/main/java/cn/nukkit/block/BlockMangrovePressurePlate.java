package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockMangrovePressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangrovePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangrovePressurePlate(BlockState blockstate) {
        super(blockstate);
        this.onPitch = 0.8f;
        this.offPitch = 0.7f;
    }

    @Override
    public String getName() {
        return "Mangrove Pressure Plate";
    }
}