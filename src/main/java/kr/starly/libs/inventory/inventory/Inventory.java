package kr.starly.libs.inventory.inventory;

import kr.starly.libs.inventory.InventoryFramework;
import kr.starly.libs.inventory.inventory.event.ItemPostUpdateEvent;
import kr.starly.libs.inventory.inventory.event.ItemPreUpdateEvent;
import kr.starly.libs.inventory.inventory.event.UpdateReason;
import kr.starly.libs.inventory.window.AbstractWindow;
import kr.starly.libs.inventory.window.Window;
import kr.starly.libs.util.ArrayUtils;
import kr.starly.libs.util.InventoryUtils;
import kr.starly.libs.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

public abstract class Inventory {

    private Set<AbstractWindow> windows;
    private Consumer<ItemPreUpdateEvent> preUpdateHandler;
    private Consumer<ItemPostUpdateEvent> postUpdateHandler;
    private @Getter
    @Setter int guiPriority = 0;

    public abstract int getSize();

    public abstract int @NotNull [] getMaxStackSizes();

    public abstract int getMaxSlotStackSize(int slot);

    public abstract @Nullable ItemStack @NotNull [] getItems();

    public abstract @Nullable ItemStack @NotNull [] getUnsafeItems();

    public abstract @Nullable ItemStack getItem(int slot);

    public abstract @Nullable ItemStack getUnsafeItem(int slot);

    protected abstract void setCloneBackingItem(int slot, @Nullable ItemStack itemStack);

    protected abstract void setDirectBackingItem(int slot, @Nullable ItemStack itemStack);

    public @NotNull Set<@NotNull Window> getWindows() {
        if (windows == null)
            return Collections.emptySet();

        return Collections.unmodifiableSet(windows);
    }

    public void addWindow(AbstractWindow window) {
        if (windows == null)
            windows = new HashSet<>();

        windows.add(window);
    }

    public void removeWindow(AbstractWindow window) {
        if (windows == null)
            return;

        windows.remove(window);
    }

    public void notifyWindows() {
        if (windows == null)
            return;

        windows.forEach(window -> window.handleInventoryUpdate(this));
    }

    public @Nullable Consumer<ItemPreUpdateEvent> getPreUpdateHandler() {
        return preUpdateHandler;
    }

    public void setPreUpdateHandler(@Nullable Consumer<@NotNull ItemPreUpdateEvent> preUpdateHandler) {
        this.preUpdateHandler = preUpdateHandler;
    }

    public @Nullable Consumer<@NotNull ItemPostUpdateEvent> getPostUpdateHandler() {
        return postUpdateHandler;
    }

    public void setPostUpdateHandler(@Nullable Consumer<@NotNull ItemPostUpdateEvent> inventoryUpdatedHandler) {
        this.postUpdateHandler = inventoryUpdatedHandler;
    }

    public boolean hasEventHandlers() {
        return preUpdateHandler != null || postUpdateHandler != null;
    }

    private boolean shouldCallEvents(@Nullable UpdateReason updateReason) {
        return hasEventHandlers() && updateReason != UpdateReason.SUPPRESSED;
    }

    public ItemPreUpdateEvent callPreUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call ItemUpdateEvent with UpdateReason.SUPPRESSED");

        ItemPreUpdateEvent event = new ItemPreUpdateEvent(this, slot, updateReason, previousItemStack, newItemStack);
        if (preUpdateHandler != null) {
            try {
                preUpdateHandler.accept(event);
            } catch (Throwable t) {
                InventoryFramework.getLogger().log(Level.SEVERE, "An exception occurred while handling an inventory event", t);
            }
        }
        return event;
    }

    public void callPostUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call InventoryUpdatedEvent with UpdateReason.SUPPRESSED");

        ItemPostUpdateEvent event = new ItemPostUpdateEvent(this, slot, updateReason, previousItemStack, newItemStack);
        if (postUpdateHandler != null) {
            try {
                postUpdateHandler.accept(event);
            } catch (Throwable t) {
                InventoryFramework.getLogger().log(Level.SEVERE, "An exception occurred while handling an inventory event", t);
            }
        }
    }

    public int getMaxStackSize(int slot) {
        ItemStack currentItem = getUnsafeItem(slot);
        int slotMaxStackSize = getMaxSlotStackSize(slot);
        if (currentItem != null) {
            return Math.min(InventoryUtils.stackSizeProvider.getMaxStackSize(currentItem), slotMaxStackSize);
        } else {
            return slotMaxStackSize;
        }
    }

    public int getMaxStackSize(int slot, int alternative) {
        ItemStack currentItem = getUnsafeItem(slot);
        int slotMaxStackSize = getMaxSlotStackSize(slot);
        return Math.min(currentItem != null ? InventoryUtils.stackSizeProvider.getMaxStackSize(currentItem) : alternative, slotMaxStackSize);
    }

    public int getMaxStackSize(int slot, @Nullable ItemStack alternativeFrom) {
        int itemMaxStackSize = alternativeFrom == null ? 64 : InventoryUtils.stackSizeProvider.getMaxStackSize(alternativeFrom);
        return getMaxStackSize(slot, itemMaxStackSize);
    }

    public int getMaxSlotStackSize(int slot, int alternative) {
        return Math.min(alternative, getMaxSlotStackSize(slot));
    }

    public int getMaxSlotStackSize(int slot, @Nullable ItemStack alternativeFrom) {
        int itemMaxStackSize = alternativeFrom == null ? 64 : InventoryUtils.stackSizeProvider.getMaxStackSize(alternativeFrom);
        return getMaxSlotStackSize(slot, itemMaxStackSize);
    }

    public boolean isSynced(int slot, ItemStack assumedStack) {
        ItemStack actualStack = getUnsafeItem(slot);
        return Objects.equals(actualStack, assumedStack);
    }

    public boolean isFull() {
        ItemStack[] items = getUnsafeItems();
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item == null || item.getAmount() < getMaxStackSize(slot))
                return false;
        }

        return true;
    }

    public boolean isEmpty() {
        for (ItemStack item : getUnsafeItems())
            if (item != null) return false;

        return true;
    }

    public boolean hasEmptySlot() {
        for (ItemStack item : getUnsafeItems())
            if (item == null) return true;

        return false;
    }

    public boolean contains(Predicate<ItemStack> predicate) {
        for (ItemStack item : getUnsafeItems()) {
            if (item != null && predicate.test(item.clone()))
                return true;
        }

        return false;
    }

    public boolean containsSimilar(ItemStack itemStack) {
        for (ItemStack item : getUnsafeItems()) {
            if (item != null && item.isSimilar(itemStack))
                return true;
        }

        return false;
    }

    public int count(Predicate<ItemStack> predicate) {
        int count = 0;
        for (ItemStack item : getUnsafeItems()) {
            if (item != null && predicate.test(item.clone()))
                count++;
        }

        return count;
    }

    public int countSimilar(ItemStack itemStack) {
        int count = 0;
        for (ItemStack item : getUnsafeItems()) {
            if (item != null && item.isSimilar(itemStack))
                count++;
        }

        return count;
    }

    public boolean hasItem(int slot) {
        return getUnsafeItem(slot) != null;
    }

    public int getItemAmount(int slot) {
        ItemStack currentStack = getUnsafeItem(slot);
        return currentStack != null ? currentStack.getAmount() : 0;
    }

    public void setItemSilently(int slot, @Nullable ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            itemStack = null;

        setCloneBackingItem(slot, itemStack);
        notifyWindows();
    }

    public boolean forceSetItem(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack itemStack) {
        if (!shouldCallEvents(updateReason)) {
            setItemSilently(slot, itemStack);
            return true;
        } else {
            ItemStack previousStack = getItem(slot);
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, previousStack, itemStack != null ? itemStack.clone() : null);
            if (!event.isCancelled()) {
                ItemStack newStack = event.getNewItem();
                setItemSilently(slot, newStack);
                callPostUpdateEvent(updateReason, slot, previousStack, newStack);
                return true;
            }
            return false;
        }
    }

    public boolean setItem(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return forceSetItem(updateReason, slot, null);

        int maxStackSize = getMaxSlotStackSize(slot, itemStack);
        if (itemStack.getAmount() > maxStackSize)
            return false;

        return forceSetItem(updateReason, slot, itemStack);
    }

    public boolean modifyItem(@Nullable UpdateReason updateReason, int slot, @NotNull Consumer<@Nullable ItemStack> modifier) {
        ItemStack itemStack = getItem(slot);
        modifier.accept(itemStack);
        return setItem(updateReason, slot, itemStack);
    }

    public boolean replaceItem(@Nullable UpdateReason updateReason, int slot, @NotNull Function<@Nullable ItemStack, @Nullable ItemStack> function) {
        ItemStack currentStack = getItem(slot);
        ItemStack newStack = function.apply(currentStack);
        return setItem(updateReason, slot, newStack);
    }

    public int putItem(@Nullable UpdateReason updateReason, int slot, @NotNull ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return 0;

        ItemStack currentStack = getUnsafeItem(slot);
        if (currentStack == null || currentStack.isSimilar(itemStack)) {
            int currentAmount = currentStack == null ? 0 : currentStack.getAmount();
            int maxStackSize = getMaxStackSize(slot, itemStack);
            if (currentAmount < maxStackSize) {
                int additionalAmount = itemStack.getAmount();
                int newAmount = Math.min(currentAmount + additionalAmount, maxStackSize);

                ItemStack newItemStack = itemStack.clone();
                newItemStack.setAmount(newAmount);

                if (shouldCallEvents(updateReason)) {
                    ItemStack currentStackC = currentStack != null ? currentStack.clone() : null;
                    ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStackC, newItemStack);
                    if (!event.isCancelled()) {
                        newItemStack = event.getNewItem();
                        setCloneBackingItem(slot, newItemStack);
                        notifyWindows();

                        int newAmountEvent = newItemStack != null ? newItemStack.getAmount() : 0;
                        int remaining = itemStack.getAmount() - (newAmountEvent - currentAmount);

                        callPostUpdateEvent(updateReason, slot, currentStackC, newItemStack);

                        return remaining;
                    }
                } else {
                    setDirectBackingItem(slot, newItemStack);
                    notifyWindows();
                    return additionalAmount - (newAmount - currentAmount);
                }
            }
        }

        return itemStack.getAmount();
    }

    public int setItemAmount(@Nullable UpdateReason updateReason, int slot, int amount) {
        ItemStack currentStack = getUnsafeItem(slot);
        if (currentStack == null)
            throw new IllegalStateException("There is no ItemStack on that slot");

        int maxStackSize = getMaxStackSize(slot);

        ItemStack newItemStack;
        if (amount > 0) {
            newItemStack = currentStack.clone();
            newItemStack.setAmount(Math.min(amount, maxStackSize));
        } else {
            newItemStack = null;
        }

        if (shouldCallEvents(updateReason)) {
            ItemStack currentStackC = currentStack != null ? currentStack.clone() : null;
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStackC, newItemStack);
            if (!event.isCancelled()) {
                newItemStack = event.getNewItem();
                setCloneBackingItem(slot, newItemStack);
                notifyWindows();

                int actualAmount = newItemStack != null ? newItemStack.getAmount() : 0;

                callPostUpdateEvent(updateReason, slot, currentStackC, newItemStack);

                return actualAmount;
            }
        } else {
            setDirectBackingItem(slot, newItemStack);
            notifyWindows();
            return amount;
        }

        return currentStack.getAmount();
    }

    public int addItemAmount(@Nullable UpdateReason updateReason, int slot, int amount) {
        ItemStack currentStack = getUnsafeItem(slot);
        if (currentStack == null)
            return 0;

        int currentAmount = currentStack.getAmount();
        return setItemAmount(updateReason, slot, currentAmount + amount) - currentAmount;
    }

    public int addItem(@Nullable UpdateReason updateReason, @NotNull ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return 0;

        ItemStack[] items = getUnsafeItems();

        int originalAmount = itemStack.getAmount();
        int amountLeft = originalAmount;

        amountLeft = addToPartialSlots(updateReason, itemStack, amountLeft, items);
        amountLeft = addToEmptySlots(updateReason, itemStack, amountLeft, items);

        if (originalAmount != amountLeft)
            notifyWindows();

        return amountLeft;
    }

    private int addToPartialSlots(
            @Nullable UpdateReason updateReason,
            @NotNull ItemStack itemStack,
            int amountLeft,
            @Nullable ItemStack @NotNull [] items
    ) {
        for (int slot = 0; slot < items.length; slot++) {
            if (amountLeft <= 0)
                break;

            ItemStack currentStack = items[slot];
            if (currentStack == null)
                continue;
            int maxStackSize = getMaxSlotStackSize(slot, itemStack);
            if (currentStack.getAmount() >= maxStackSize)
                continue;
            if (!itemStack.isSimilar(currentStack))
                continue;

            ItemStack newStack = itemStack.clone();
            newStack.setAmount(Math.min(currentStack.getAmount() + amountLeft, maxStackSize));
            if (shouldCallEvents(updateReason)) {
                ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStack.clone(), newStack);
                if (!event.isCancelled()) {
                    newStack = event.getNewItem();
                    setCloneBackingItem(slot, newStack);
                    callPostUpdateEvent(updateReason, slot, currentStack.clone(), newStack);

                    int newStackAmount = newStack != null ? newStack.getAmount() : 0;
                    amountLeft -= newStackAmount - currentStack.getAmount();
                }
            } else {
                setDirectBackingItem(slot, newStack);
                amountLeft -= newStack.getAmount() - currentStack.getAmount();
            }
        }

        return amountLeft;
    }

    private int addToEmptySlots(
            @Nullable UpdateReason updateReason,
            @NotNull ItemStack itemStack,
            int amountLeft,
            @Nullable ItemStack @NotNull [] items
    ) {
        for (int slot = 0; slot < items.length; slot++) {
            if (amountLeft <= 0)
                break;

            if (items[slot] != null)
                continue;

            ItemStack newStack = itemStack.clone();
            newStack.setAmount(Math.min(amountLeft, getMaxSlotStackSize(slot, itemStack)));
            if (shouldCallEvents(updateReason)) {
                ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, null, newStack);
                if (!event.isCancelled()) {
                    newStack = event.getNewItem();
                    setCloneBackingItem(slot, newStack);
                    callPostUpdateEvent(updateReason, slot, null, newStack);

                    int newStackAmount = newStack != null ? newStack.getAmount() : 0;
                    amountLeft -= newStackAmount;
                }
            } else {
                setDirectBackingItem(slot, newStack);
                amountLeft -= newStack.getAmount();
            }
        }

        return amountLeft;
    }

    public int[] simulateAdd(@NotNull ItemStack first, @NotNull ItemStack @NotNull ... rest) {
        if (rest.length == 0) {
            return new int[]{simulateSingleAdd(first)};
        } else {
            ItemStack[] allStacks = ArrayUtils.concat(first, rest);
            return simulateMultiAdd(Arrays.asList(allStacks));
        }
    }

    public int[] simulateAdd(@NotNull List<@NotNull ItemStack> itemStacks) {
        if (itemStacks.isEmpty())
            return new int[0];

        if (itemStacks.size() == 1) {
            return new int[]{simulateSingleAdd(itemStacks.getFirst())};
        } else {
            return simulateMultiAdd(itemStacks);
        }
    }

    public boolean canHold(@NotNull ItemStack first, @NotNull ItemStack @NotNull ... rest) {
        if (rest.length == 0) {
            return simulateSingleAdd(first) == 0;
        } else {
            ItemStack[] allStacks = ArrayUtils.concat(first, rest);
            return Arrays.stream(simulateMultiAdd(Arrays.asList(allStacks))).allMatch(i -> i == 0);
        }
    }

    public boolean canHold(@NotNull List<@NotNull ItemStack> itemStacks) {
        if (itemStacks.isEmpty()) return true;

        if (itemStacks.size() == 1) {
            return simulateSingleAdd(itemStacks.getFirst()) == 0;
        } else {
            return Arrays.stream(simulateMultiAdd(itemStacks)).allMatch(i -> i == 0);
        }
    }

    public int simulateSingleAdd(@NotNull ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return 0;

        ItemStack[] items = getUnsafeItems();
        int amountLeft = itemStack.getAmount();

        for (int slot = 0; slot < items.length; slot++) {
            if (amountLeft == 0)
                break;

            ItemStack currentStack = items[slot];
            if (currentStack == null)
                continue;
            int maxStackSize = getMaxSlotStackSize(slot, itemStack);
            if (currentStack.getAmount() >= maxStackSize)
                continue;
            if (!itemStack.isSimilar(currentStack))
                continue;

            amountLeft = Math.max(0, amountLeft - (maxStackSize - currentStack.getAmount()));
        }

        for (int slot = 0; slot < items.length; slot++) {
            if (amountLeft == 0)
                break;

            if (items[slot] != null)
                continue;

            int maxStackSize = getMaxStackSize(slot, itemStack);
            amountLeft -= Math.min(amountLeft, maxStackSize);
        }

        return amountLeft;
    }

    public int[] simulateMultiAdd(@NotNull List<@NotNull ItemStack> itemStacks) {
        Inventory copy = new VirtualInventory(null, getSize(), getItems(), getMaxStackSizes().clone());
        int[] result = new int[itemStacks.size()];
        for (int index = 0; index != itemStacks.size(); index++) {
            result[index] = copy.addItem(UpdateReason.SUPPRESSED, itemStacks.get(index));
        }

        return result;
    }

    public int collectSimilar(@Nullable UpdateReason updateReason, @NotNull ItemStack itemStack) {
        return collectSimilar(updateReason, itemStack, itemStack.getAmount());
    }

    public int collectSimilar(@Nullable UpdateReason updateReason, @NotNull ItemStack template, int baseAmount) {
        int amount = baseAmount;
        int maxStackSize = InventoryUtils.stackSizeProvider.getMaxStackSize(template);
        if (amount < maxStackSize) {
            ItemStack[] items = getUnsafeItems();

            // find partial slots and take items from there
            for (int slot = 0; slot < items.length; slot++) {
                ItemStack currentStack = items[slot];
                if (currentStack == null || currentStack.getAmount() >= maxStackSize || !template.isSimilar(currentStack))
                    continue;

                amount += takeFrom(updateReason, slot, maxStackSize - amount);
                if (amount == maxStackSize)
                    return amount;
            }

            for (int slot = 0; slot < items.length; slot++) {
                ItemStack currentStack = items[slot];
                if (currentStack == null || currentStack.getAmount() <= maxStackSize || !template.isSimilar(currentStack))
                    continue;

                amount += takeFrom(updateReason, slot, maxStackSize - amount);
                if (amount == maxStackSize)
                    return amount;
            }
        }

        return amount;
    }

    public int removeIf(@Nullable UpdateReason updateReason, @NotNull Predicate<@NotNull ItemStack> predicate) {
        ItemStack[] items = getUnsafeItems();

        int removed = 0;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && predicate.test(item.clone()) && setItem(updateReason, slot, null)) {
                removed += item.getAmount();
            }
        }

        return removed;
    }

    public int removeFirst(@Nullable UpdateReason updateReason, int amount, @NotNull Predicate<@NotNull ItemStack> predicate) {
        ItemStack[] items = getUnsafeItems();

        int leftOver = amount;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && predicate.test(item.clone())) {
                leftOver -= takeFrom(updateReason, slot, leftOver);
                if (leftOver == 0) return 0;
            }
        }

        return amount - leftOver;
    }

    public int removeSimilar(@Nullable UpdateReason updateReason, @NotNull ItemStack itemStack) {
        ItemStack[] items = getUnsafeItems();

        int removed = 0;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && item.isSimilar(itemStack) && setItem(updateReason, slot, null)) {
                removed += item.getAmount();
            }
        }

        return removed;
    }

    public int removeFirstSimilar(@Nullable UpdateReason updateReason, int amount, @NotNull ItemStack itemStack) {
        ItemStack[] items = getUnsafeItems();

        int leftOver = amount;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && item.isSimilar(itemStack)) {
                leftOver -= takeFrom(updateReason, slot, leftOver);
                if (leftOver == 0) return 0;
            }
        }

        return amount - leftOver;
    }

    private int takeFrom(@Nullable UpdateReason updateReason, int slot, int maxTake) {
        ItemStack currentItemStack = getUnsafeItem(slot);
        int amount = currentItemStack.getAmount();
        int take = Math.min(amount, maxTake);

        ItemStack newItemStack;
        if (take != amount) {
            newItemStack = currentItemStack.clone();
            newItemStack.setAmount(amount - take);
        } else newItemStack = null;

        if (shouldCallEvents(updateReason)) {
            ItemStack currentItemStackC = currentItemStack.clone();
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentItemStackC, newItemStack);
            if (!event.isCancelled()) {
                newItemStack = event.getNewItem();
                setCloneBackingItem(slot, newItemStack);
                notifyWindows();

                int amountTaken = currentItemStack.getAmount() - (newItemStack == null ? 0 : newItemStack.getAmount());

                callPostUpdateEvent(updateReason, slot, currentItemStackC, newItemStack);

                return amountTaken;
            }
        } else {
            setDirectBackingItem(slot, newItemStack);
            notifyWindows();
            return take;
        }

        return 0;
    }
}