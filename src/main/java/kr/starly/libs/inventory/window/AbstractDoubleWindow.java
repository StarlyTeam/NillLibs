package kr.starly.libs.inventory.window;

import kr.starly.libs.inventory.gui.AbstractGui;
import kr.starly.libs.inventory.gui.SlotElement;
import kr.starly.libs.nms.NmsMultiVersion;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.util.Pair;
import kr.starly.libs.util.SlotUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class AbstractDoubleWindow extends AbstractWindow {

    @Getter
    private final Inventory playerInventory;
    private final ItemStack[] playerItems = new ItemStack[36];
    @Getter
    protected Inventory upperInventory;

    public AbstractDoubleWindow(Player player, ComponentWrapper title, int size, Inventory upperInventory, boolean closeable) {
        super(player, title, size, closeable);
        this.upperInventory = upperInventory;
        this.playerInventory = player.getInventory();
    }

    @Override
    protected void initItems() {
        for (int i = 0; i < upperInventory.getSize(); i++) {
            SlotElement element = getSlotElement(i);
            redrawItem(i, element, true);
        }

        Inventory inventory = getViewer().getInventory();
        for (int i = 0; i < 36; i++) {
            playerItems[i] = inventory.getItem(i);
            inventory.setItem(i, null);
        }

        for (int i = upperInventory.getSize(); i < upperInventory.getSize() + 36; i++) {
            SlotElement element = getSlotElement(i);
            redrawItem(i, element, true);
        }
    }

    @Override
    public @Nullable ItemStack @Nullable [] getPlayerItems() {
        if (isOpen()) {
            return playerItems;
        }

        return null;
    }

    private void restorePlayerInventory() {
        Inventory inventory = getViewer().getInventory();
        for (int i = 0; i < 36; i++) {
            inventory.setItem(i, playerItems[i]);
        }
    }

    @Override
    protected void redrawItem(int index, SlotElement element, boolean setItem) {
        super.redrawItem(index, element, setItem);
    }

    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
        if (slot >= upperInventory.getSize()) {
            if (isOpen()) {
                int invSlot = SlotUtils.translateGuiToPlayerInv(slot - upperInventory.getSize());
                setPlayerInvItem(invSlot, itemStack);
            }
        } else setUpperInvItem(slot, itemStack);
    }

    protected void setUpperInvItem(int slot, ItemStack itemStack) {
        upperInventory.setItem(slot, itemStack);
    }

    protected void setPlayerInvItem(int slot, ItemStack itemStack) {
        playerInventory.setItem(slot, itemStack);
    }

    @Override
    public void handleViewerDeath(PlayerDeathEvent event) {
        if (isOpen()) {
            List<ItemStack> drops = event.getDrops();
            if (!event.getKeepInventory()) {
                drops.clear();
                Arrays.stream(playerItems)
                        .filter(Objects::nonNull)
                        .forEach(drops::add);
            }
        }
    }

    @Override
    protected void handleOpened() {
        NmsMultiVersion.getPlayerUtils().stopAdvancementListening(getViewer());
    }

    @Override
    protected void handleClosed() {
        restorePlayerInventory();
        NmsMultiVersion.getPlayerUtils().startAdvancementListening(getViewer());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Pair<AbstractGui, Integer> clicked = getWhereClicked(event);
        clicked.getFirst().handleClick(clicked.getSecond(), (Player) event.getWhoClicked(), event.getClick(), event);
    }

    @Override
    public void handleItemShift(InventoryClickEvent event) {
    }

    @Override
    public Inventory[] getInventories() {
        return isOpen() ? new Inventory[]{upperInventory, playerInventory} : new Inventory[]{upperInventory};
    }

    protected abstract Pair<AbstractGui, Integer> getWhereClicked(InventoryClickEvent event);
}