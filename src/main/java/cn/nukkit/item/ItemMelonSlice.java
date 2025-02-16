package cn.nukkit.item;

public class ItemMelonSlice extends ItemEdible {
    public ItemMelonSlice() {
        this(0, 1);
    }

    public ItemMelonSlice(Integer meta) {
        this(meta, 1);
    }

    public ItemMelonSlice(Integer meta, int count) {
        super(MELON_SLICE, meta, count, "Melon Slice");
    }
}