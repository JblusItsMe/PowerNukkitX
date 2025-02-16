package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/8
 */
public class BlockLadder extends BlockTransparent implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(LADDER, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLadder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLadder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Ladder";
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    private double offMinX;
    private double offMinZ;
    private double offMaxX;
    private double offMaxZ;

    private void calculateOffsets() {
        double f = 0.1875;
        switch (this.getBlockFace()) {
            case NORTH -> {
                this.offMinX = 0;
                this.offMinZ = 1 - f;
                this.offMaxX = 1;
                this.offMaxZ = 1;
            }
            case SOUTH -> {
                this.offMinX = 0;
                this.offMinZ = 0;
                this.offMaxX = 1;
                this.offMaxZ = f;
            }
            case WEST -> {
                this.offMinX = 1 - f;
                this.offMinZ = 0;
                this.offMaxX = 1;
                this.offMaxZ = 1;
            }
            case EAST -> {
                this.offMinX = 0;
                this.offMinZ = 0;
                this.offMaxX = f;
                this.offMaxZ = 1;
            }
            default -> {
                this.offMinX = 0;
                this.offMinZ = 1;
                this.offMaxX = 1;
                this.offMaxZ = 1;
            }
        }
    }

    @Override
    public double getMinX() {
        return this.x + offMinX;
    }

    @Override
    public double getMinZ() {
        return this.z + offMinZ;
    }

    @Override
    public double getMaxX() {
        return this.x + offMaxX;
    }

    @Override
    public double getMaxZ() {
        return this.z + offMaxZ;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return super.recalculateBoundingBox();
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target instanceof BlockLadder) {
            var opposite = face.getOpposite();
            var oppositeB = this.getLevel().getBlock(target.add(face.getUnitVector()));
            var targetBlock = this.getLevel().getBlock(target.add(face.getUnitVector().multiply(2)));
            if (isSupportValid(targetBlock, opposite)) {
                //不设置damage是因为level#useItemOn中有逻辑设置
                this.getLevel().setBlock(oppositeB, this, true, false);
                return true;
            }
        }
        if (face.getHorizontalIndex() == -1 || !isSupportValid(target, face)) {
            return false;
        }
        //不设置damage是因为level#useItemOn中有逻辑设置
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    private boolean isSupportValid(Block support, BlockFace face) {
        if (support instanceof BlockGlassStained || support instanceof BlockBlackStainedGlassPane) return false;
        return switch (support.getId()) {
            case GLASS, GLASS_PANE, BEACON -> false;
            default -> BlockLever.isSupportValid(support, face);
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = getBlockFace();
            if (!isSupportValid(this.getSide(face), face.getOpposite())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.getItemBlock(LADDER, 0, 1)
        };
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
        calculateOffsets();
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
