package kr.starly.libs.inventory.window;

import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.inventory.gui.AbstractGui;
import kr.starly.libs.inventory.gui.Gui;
import kr.starly.libs.inventory.gui.SlotElement;
import kr.starly.libs.inventory.inventory.Inventory;
import kr.starly.libs.inventory.inventory.ReferencingInventory;
import kr.starly.libs.util.Pair;
import kr.starly.libs.util.SlotUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMergedWindow extends AbstractDoubleWindow {

    private final AbstractGui gui;

    public AbstractMergedWindow(Player player, ComponentWrapper title, AbstractGui gui, org.bukkit.inventory.Inventory upperInventory, boolean closeable) {
        super(player, title, gui.getSize(), upperInventory, closeable);
        this.gui = gui;
    }

    @Override
    public void handleSlotElementUpdate(Gui child, int slotIndex) {
        redrawItem(slotIndex, gui.getSlotElement(slotIndex), true);
    }

    @Override
    protected SlotElement getSlotElement(int index) {
        return gui.getSlotElement(index);
    }

    @Override
    protected Pair<AbstractGui, Integer> getWhereClicked(InventoryClickEvent event) {
        org.bukkit.inventory.Inventory clicked = event.getClickedInventory();
        int slot = event.getSlot();
        int clickedIndex = clicked == getUpperInventory() ? slot
                : getUpperInventory().getSize() + SlotUtils.translatePlayerInvToGui(slot);
        return new Pair<>(gui, clickedIndex);
    }

    @Override
    protected Pair<AbstractGui, Integer> getGuiAt(int index) {
        return index < gui.getSize() ? new Pair<>(gui, index) : null;
    }

    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[]{gui};
    }

    @Override
    protected List<Inventory> getContentInventories() {
        List<Inventory> inventories = new ArrayList<>(gui.getAllInventories());
        inventories.add(ReferencingInventory.fromStorageContents(getViewer().getInventory()));
        return inventories;
    }
}