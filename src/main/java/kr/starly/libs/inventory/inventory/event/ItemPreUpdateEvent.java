package kr.starly.libs.inventory.inventory.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kr.starly.libs.inventory.inventory.Inventory;

@Getter
@Setter
public class ItemPreUpdateEvent extends ItemUpdateEvent {

    private boolean cancelled;

    public ItemPreUpdateEvent(@NotNull Inventory inventory, int slot, @Nullable UpdateReason updateReason,
                              @Nullable ItemStack previousItem, @Nullable ItemStack newItem) {
        super(inventory, slot, updateReason, previousItem, newItem);
    }

    public void setNewItem(@Nullable ItemStack newItem) {
        this.newItemStack = newItem;
    }
}