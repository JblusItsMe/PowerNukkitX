package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * @author joserobjr
 * @since 2021-12-11
 */


public abstract class NoOpIventoryAction extends InventoryAction {


    protected NoOpIventoryAction(Item sourceItem, Item targetItem) {
        super(sourceItem, targetItem);
    }

    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {
        // Does nothing
    }

    @Override
    public void onExecuteFail(Player source) {
        // Does nothing
    }
}
