package kr.starly.libs.inventory.inventory.event;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kr.starly.libs.inventory.inventory.Inventory;

public class ItemPostUpdateEvent extends ItemUpdateEvent {

    public ItemPostUpdateEvent(@NotNull Inventory inventory, int slot, @Nullable UpdateReason updateReason,
                               @Nullable ItemStack previousItem, @Nullable ItemStack newItem) {
        super(inventory, slot, updateReason, previousItem, newItem);
    }
}