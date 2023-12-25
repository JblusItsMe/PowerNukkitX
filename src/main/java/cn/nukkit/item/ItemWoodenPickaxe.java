package cn.nukkit.item;

public class ItemWoodenPickaxe extends ItemTool {
    public ItemWoodenPickaxe() {
        this(0, 1);
    }

    public ItemWoodenPickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenPickaxe(Integer meta, int count) {
        super(WOODEN_PICKAXE, meta, count, "Wooden Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }
}