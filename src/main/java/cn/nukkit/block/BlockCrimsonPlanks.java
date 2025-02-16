package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_PLANKS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonPlanks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crimson Planks";
    }

    @Override
    public double getResistance() {
        return 3;
    }

}