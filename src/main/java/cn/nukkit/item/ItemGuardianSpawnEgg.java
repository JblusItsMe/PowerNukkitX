package cn.nukkit.item;

public class ItemGuardianSpawnEgg extends ItemSpawnEgg {
    public ItemGuardianSpawnEgg() {
        super(GUARDIAN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 49;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}