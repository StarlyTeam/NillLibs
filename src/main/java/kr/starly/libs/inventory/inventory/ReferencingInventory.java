package kr.starly.libs.inventory.inventory;

import kr.starly.libs.util.TriConsumer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReferencingInventory extends kr.starly.libs.inventory.inventory.Inventory {

    private static final int MAX_STACK_SIZE = 64;

    protected final @NotNull Inventory inventory;
    protected final @NotNull Function<@NotNull Inventory, @Nullable ItemStack @NotNull []> itemsGetter;
    protected final @NotNull BiFunction<@NotNull Inventory, @NotNull Integer, @Nullable ItemStack> itemGetter;
    protected final @NotNull TriConsumer<@NotNull Inventory, @NotNull Integer, @Nullable ItemStack> itemSetter;
    protected final int size;
    protected final int @NotNull [] maxStackSizes;

    public ReferencingInventory(
            @NotNull Inventory inventory,
            @NotNull Function<@NotNull Inventory, @Nullable ItemStack @NotNull []> itemsGetter,
            @NotNull BiFunction<@NotNull Inventory, @NotNull Integer, @Nullable ItemStack> itemGetter,
            @NotNull TriConsumer<@NotNull Inventory, @NotNull Integer, @Nullable ItemStack> itemSetter
    ) {
        this.inventory = inventory;
        this.itemsGetter = itemsGetter;
        this.itemGetter = itemGetter;
        this.itemSetter = itemSetter;
        this.size = itemsGetter.apply(inventory).length;
        this.maxStackSizes = new int[size];
        Arrays.fill(maxStackSizes, MAX_STACK_SIZE);
    }

    public static @NotNull ReferencingInventory fromStorageContents(@NotNull Inventory inventory) {
        return new ReferencingInventory(inventory, Inventory::getStorageContents, Inventory::getItem, Inventory::setItem);
    }

    public static @NotNull ReferencingInventory fromContents(@NotNull Inventory inventory) {
        return new ReferencingInventory(inventory, Inventory::getContents, Inventory::getItem, Inventory::setItem);
    }

    public static @NotNull ReferencingInventory fromReversedPlayerStorageContents(@NotNull PlayerInventory inventory) {
        return new ReversedPlayerContents(inventory);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int @NotNull [] getMaxStackSizes() {
        return maxStackSizes.clone();
    }

    @Override
    public int getMaxSlotStackSize(int slot) {
        return MAX_STACK_SIZE;
    }

    @Override
    public @Nullable ItemStack @NotNull [] getItems() {
        return itemsGetter.apply(inventory);
    }

    @Override
    public @Nullable ItemStack @NotNull [] getUnsafeItems() {
        return itemsGetter.apply(inventory);
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        return itemGetter.apply(inventory, slot);
    }

    @Override
    public @Nullable ItemStack getUnsafeItem(int slot) {
        return itemGetter.apply(inventory, slot);
    }

    @Override
    protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
        itemSetter.accept(inventory, slot, itemStack);
    }

    @Override
    protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
        itemSetter.accept(inventory, slot, itemStack);
    }

    private static class ReversedPlayerContents extends ReferencingInventory {

        public ReversedPlayerContents(PlayerInventory inventory) {
            super(inventory, Inventory::getStorageContents, Inventory::getItem, Inventory::setItem);
        }

        private int convertSlot(int InventoryFrameworkSlot) {
            if (InventoryFrameworkSlot < 9) return 8 - InventoryFrameworkSlot;
            else return 44 - InventoryFrameworkSlot;
        }

        @Override
        public @Nullable ItemStack getItem(int slot) {
            return super.getItem(convertSlot(slot));
        }

        @Override
        public @Nullable ItemStack getUnsafeItem(int slot) {
            return super.getUnsafeItem(convertSlot(slot));
        }

        @Override
        public @Nullable ItemStack @NotNull [] getUnsafeItems() {
            return getItems();
        }

        @Override
        public @Nullable ItemStack @NotNull [] getItems() {
            ItemStack[] items = itemsGetter.apply(inventory);
            ItemStack[] reorderedItems = new ItemStack[items.length];

            for (int i = 0; i < 9; i++) {
                reorderedItems[8 - i] = items[i];
            }

            for (int i = 9; i < 36; i++) {
                reorderedItems[44 - i] = items[i];
            }

            return reorderedItems;
        }

        @Override
        protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
            super.setCloneBackingItem(convertSlot(slot), itemStack);
        }

        @Override
        protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
            super.setDirectBackingItem(convertSlot(slot), itemStack);
        }
    }
}