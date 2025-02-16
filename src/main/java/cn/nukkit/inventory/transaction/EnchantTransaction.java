package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.EnchantItemEvent;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.action.EnchantingAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;

import java.util.List;


public class EnchantTransaction extends InventoryTransaction {
    private Item inputItem;
    private Item outputItem;
    private int cost = -1;

    public EnchantTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction slotChangeAction) {
                if (slotChangeAction.getInventory() instanceof EnchantInventory && slotChangeAction.getSlot() == 0) {
                    this.outputItem = slotChangeAction.getTargetItem();
                }
            }
        }
    }

    @Override
    public boolean canExecute() {
        Inventory inv = getSource().getWindowById(Player.ENCHANT_WINDOW_ID);
        if (inv == null) return false;
        EnchantInventory eInv = (EnchantInventory) inv;
        if (!getSource().isCreative()) {
            if (cost == -1 || !isLapisLazuli(eInv.getReagentSlot()) || eInv.getReagentSlot().count < cost)
                return false;
        }
        return inputItem != null && outputItem != null
                && inputItem.equals(eInv.getInputSlot(), true, true)
                && inputItem.getId() == outputItem.getId()
                && inputItem.getCount() == outputItem.getCount();
    }
    
    private boolean isLapisLazuli(Item item) {
        return (item instanceof ItemDye) && ((ItemDye) item).isLapisLazuli();
    }

    @Override
    public boolean execute() {
        // This will validate the enchant conditions
        if (this.hasExecuted || !this.canExecute()) {
            source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }
        EnchantInventory inv = (EnchantInventory) getSource().getWindowById(Player.ENCHANT_WINDOW_ID);
        EnchantItemEvent ev = new EnchantItemEvent(inv, inputItem, outputItem, cost, source);
        source.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            source.removeAllWindows(false);
            this.sendInventories();

            // Cancelled by plugin, means handled OK
            return true;
        }
        // This will process all the slot changes
        for (InventoryAction a : this.actions) {
            if (a.execute(source)) {
                a.onExecuteSuccess(source);
            } else {
                a.onExecuteFail(source);
            }
        }

        if (!ev.getNewItem().equals(this.outputItem, true, true)) {
            // Plugin changed item, so the previous slot change is going to be invalid
            // Send the replaced item to the enchant inventory manually
            inv.setItemByPlayer(source, 0, ev.getNewItem(), true);
        }

        if (!source.isCreative()) {
            source.setExperience(source.getExperience(), source.getExperienceLevel() - ev.getXpCost());
        }
        return true;
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if (action instanceof EnchantingAction) {
            switch (((EnchantingAction) action).getType()) {
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_INPUT ->
                        this.inputItem = action.getTargetItem(); // Input sent as newItem
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_OUTPUT ->
                        this.outputItem = action.getSourceItem(); // Output sent as oldItem
                case NetworkInventoryAction.SOURCE_TYPE_ENCHANT_MATERIAL -> {
                    if (action.getTargetItem().equals(Item.AIR_ITEM, false, false)) {
                        this.cost = action.getSourceItem().count;
                    } else {
                        this.cost = action.getSourceItem().count - action.getTargetItem().count;
                    }
                }
            }

        }
    }

    public boolean checkForEnchantPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof EnchantingAction) return true;
        }
        return false;
    }

    public Item getInputItem() {
        return inputItem;
    }

    public void setInputItem(Item inputItem) {
        this.inputItem = inputItem;
    }

    public Item getOutputItem() {
        return outputItem;
    }

    public void setOutputItem(Item outputItem) {
        this.outputItem = outputItem;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
