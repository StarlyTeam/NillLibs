package kr.starly.libs.inventory.gui.structure;

import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.gui.SlotElement;
import kr.starly.libs.inventory.gui.SlotElement.InventorySlotElement;
import kr.starly.libs.inventory.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class InventorySlotElementSupplier implements Supplier<InventorySlotElement> {

    private final Inventory inventory;
    private final ItemProvider background;
    private int slot = -1;

    public InventorySlotElementSupplier(@NotNull Inventory inventory) {
        this.inventory = inventory;
        this.background = null;
    }

    public InventorySlotElementSupplier(@NotNull Inventory inventory, @Nullable ItemProvider background) {
        this.inventory = inventory;
        this.background = background;
    }

    @Override
    public @NotNull SlotElement.InventorySlotElement get() {
        if (++slot == inventory.getSize()) slot = 0;
        return new InventorySlotElement(inventory, slot, background);
    }
}