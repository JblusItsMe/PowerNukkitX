package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * Alias STILL LAVA
 *
 * @author Angelic47 (Nukkit Project)
 */
public class BlockLava extends BlockFlowingLava {
    public static final BlockProperties PROPERTIES = new BlockProperties(LAVA, CommonBlockProperties.LIQUID_DEPTH);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLava() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLava(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Still Lava";
    }

    @Override
    public BlockLiquid getBlock() {
        return (BlockLiquid) Block.get(blockstate);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return this.getLevel().setBlock(this, this, true, false);
    }
}
