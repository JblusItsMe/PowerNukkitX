package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BlockSculk extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculk() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculk(BlockState blockstate) {
        super(blockstate);
    }
    @Override
    public String getName() {
        return "Sculk";
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        if (canHarvest(item)) {
            return super.calculateBreakTime(item, player);
        } else {
            return 1;
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
