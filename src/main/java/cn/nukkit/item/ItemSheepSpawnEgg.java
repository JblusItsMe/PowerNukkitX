package cn.nukkit.item;

public class ItemSheepSpawnEgg extends ItemSpawnEgg {
    public ItemSheepSpawnEgg() {
        super(SHEEP_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 13;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}