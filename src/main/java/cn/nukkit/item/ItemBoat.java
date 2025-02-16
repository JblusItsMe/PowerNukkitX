package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * @author yescallop
 * @since 2016/2/13
 */
public class ItemBoat extends Item {

    public ItemBoat() {
        this(0, 1);
    }

    public ItemBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemBoat(Integer meta, int count) {
        super(BOAT, meta, count);
        adjustName();
    }

    public ItemBoat(String id) {
        super(id);
    }

    @Override
    public void setAux(Integer aux) {
        super.setAux(aux);
        adjustName();
    }

    private void adjustName() {
        switch (getAux()) {
            case 0:
                name = "Oak Boat";
                return;
            case 1:
                name = "Spruce Boat";
                return;
            case 2:
                name = "Birch Boat";
                return;
            case 3:
                name = "Jungle Boat";
                return;
            case 4:
                name = "Acacia Boat";
                return;
            case 5:
                name = "Dark Oak Boat";
                return;
            case 6:
                name = "Mangrove Boat";
                return;
            case 7:
                name = "Bamboo Raft";
                return;
            case 8:
                name = "Cherry Boat";
                return;
            default:
                name = "Boat";
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public int getBoatId() {
        return this.aux;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (face != BlockFace.UP || block instanceof BlockFlowingWater) return false;
        EntityBoat boat = (EntityBoat) Entity.createEntity("Boat",
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), new CompoundTag("")
                        .putList(new ListTag<DoubleTag>("Pos")
                                .add(new DoubleTag("", block.getX() + 0.5))
                                .add(new DoubleTag("", block.getY() - (target instanceof BlockFlowingWater ? 0.375 : 0)))
                                .add(new DoubleTag("", block.getZ() + 0.5)))
                        .putList(new ListTag<DoubleTag>("Motion")
                                .add(new DoubleTag("", 0))
                                .add(new DoubleTag("", 0))
                                .add(new DoubleTag("", 0)))
                        .putList(new ListTag<FloatTag>("Rotation")
                                .add(new FloatTag("", (float) ((player.yaw + 90f) % 360)))
                                .add(new FloatTag("", 0)))
                        .putInt("Variant", getBoatId())
        );

        if (boat == null) {
            return false;
        }

        if (player.isSurvival() || player.isAdventure()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        boat.spawnToAll();
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
