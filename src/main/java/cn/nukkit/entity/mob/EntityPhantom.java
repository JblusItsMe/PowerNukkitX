package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityPhantom extends EntityMob implements EntityFlyable, EntitySmite {

    @Override
    public @NotNull String getIdentifier() {
        return PHANTOM;
    }

    public EntityPhantom(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }


    @Override
    public String getOriginalName() {
        return "Phantom";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(470)};
    }


    @Override
    public boolean isUndead() {
        return true;
    }


    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }
}
