package kr.starly.libs.inventory.inventory.event;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kr.starly.libs.inventory.inventory.Inventory;

abstract class ItemUpdateEvent {

    private final Inventory inventory;
    private final UpdateReason updateReason;
    private final @Getter int slot;
    private final ItemStack previousItemStack;
    protected ItemStack newItemStack;

    public ItemUpdateEvent(@NotNull Inventory inventory, int slot, @Nullable UpdateReason updateReason,
                           @Nullable ItemStack previousItem, @Nullable ItemStack newItem) {

        this.inventory = inventory;
        this.slot = slot;
        this.updateReason = updateReason;
        this.previousItemStack = previousItem;
        this.newItemStack = newItem;
    }

    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public @Nullable UpdateReason getUpdateReason() {
        return updateReason;
    }

    public @Nullable ItemStack getPreviousItem() {
        return previousItemStack;
    }

    public @Nullable ItemStack getNewItem() {
        return newItemStack;
    }

    public boolean isAdd() {
        if (newItemStack != null && previousItemStack != null && newItemStack.isSimilar(previousItemStack)) {
            return newItemStack.getAmount() > previousItemStack.getAmount();
        } else return previousItemStack == null && newItemStack != null;
    }

    public boolean isRemove() {
        if (newItemStack != null && previousItemStack != null && newItemStack.isSimilar(previousItemStack)) {
            return newItemStack.getAmount() < previousItemStack.getAmount();
        } else return newItemStack == null && previousItemStack != null;
    }

    public boolean isSwap() {
        return newItemStack != null && previousItemStack != null && !newItemStack.isSimilar(previousItemStack);
    }

    public int getRemovedAmount() {
        if (!isRemove())
            throw new IllegalStateException("No items have been removed");

        if (newItemStack == null) return previousItemStack.getAmount();
        else return previousItemStack.getAmount() - newItemStack.getAmount();
    }

    public int getAddedAmount() {
        if (!isAdd())
            throw new IllegalStateException("No items have been added");

        if (previousItemStack == null) return newItemStack.getAmount();
        else return newItemStack.getAmount() - previousItemStack.getAmount();
    }
}