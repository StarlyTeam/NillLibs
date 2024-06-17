package kr.starly.libs.inventory.window;

import kr.starly.libs.nms.NmsMultiVersion;
import kr.starly.libs.nms.component.BungeeComponentWrapper;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.inventory.InventoryFramework;
import kr.starly.libs.inventory.gui.AbstractGui;
import kr.starly.libs.inventory.gui.Gui;
import kr.starly.libs.inventory.gui.GuiParent;
import kr.starly.libs.inventory.gui.SlotElement;
import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.inventory.CompositeInventory;
import kr.starly.libs.inventory.inventory.Inventory;
import kr.starly.libs.inventory.inventory.event.PlayerUpdateReason;
import kr.starly.libs.inventory.inventory.event.UpdateReason;
import kr.starly.libs.scheduler.Do;
import kr.starly.libs.util.ArrayUtils;
import kr.starly.libs.util.Pair;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public abstract class AbstractWindow implements Window, GuiParent {

    private static final NamespacedKey SLOT_KEY = new NamespacedKey(InventoryFramework.getInstance().getPlugin(), "slot");

    private final Player viewer;
    private final UUID viewerUUID;
    private final SlotElement[] elementsDisplayed;
    private List<Runnable> openHandlers;
    private List<Runnable> closeHandlers;
    private List<Consumer<InventoryClickEvent>> outsideClickHandlers;
    private ComponentWrapper title;
    private boolean closeable;
    private boolean currentlyOpen;
    private boolean hasHandledClose;

    public AbstractWindow(Player viewer, ComponentWrapper title, int size, boolean closeable) {
        this.viewer = viewer;
        this.viewerUUID = viewer.getUniqueId();
        this.title = title;
        this.closeable = closeable;
        this.elementsDisplayed = new SlotElement[size];
    }

    protected void redrawItem(int index) {
        redrawItem(index, getSlotElement(index), false);
    }

    protected void redrawItem(int index, SlotElement element, boolean setItem) {
        ItemStack itemStack;
        if (element == null || (element instanceof SlotElement.InventorySlotElement && element.getItemStack() == null)) {
            ItemProvider background = getGuiAt(index).getFirst().getBackground();
            itemStack = background == null ? null : background.get();
        } else if (element instanceof SlotElement.LinkedSlotElement && element.getHoldingElement() == null) {
            ItemProvider background = null;

            List<Gui> guis = ((SlotElement.LinkedSlotElement) element).getGuiList();
            guis.addFirst(getGuiAt(index).getFirst());

            for (int i = guis.size() - 1; i >= 0; i--) {
                background = guis.get(i).getBackground();
                if (background != null) break;
            }

            itemStack = background == null ? null : background.get();
        } else {
            SlotElement holdingElement = element.getHoldingElement();
            itemStack = holdingElement.getItemStack();

            if (holdingElement instanceof SlotElement.ItemSlotElement) {
                if (itemStack.hasItemMeta()) {
                    itemStack = itemStack.clone();

                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.getPersistentDataContainer().set(SLOT_KEY, PersistentDataType.BYTE, (byte) index);
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }
        setInvItem(index, itemStack);

        if (setItem) {
            SlotElement previousElement = elementsDisplayed[index];
            if (previousElement instanceof SlotElement.ItemSlotElement itemSlotElement) {
                Item item = itemSlotElement.getItem();
                if (getItemSlotElements(item).size() == 1) {
                    item.removeWindow(this);
                }
            } else if (previousElement instanceof SlotElement.InventorySlotElement invSlotElement) {
                Inventory inventory = invSlotElement.getInventory();
                if (getInvSlotElements(invSlotElement.getInventory()).size() == 1) {
                    inventory.removeWindow(this);
                }
            }

            if (element != null) {
                SlotElement holdingElement = element.getHoldingElement();
                if (holdingElement instanceof SlotElement.ItemSlotElement) {
                    ((SlotElement.ItemSlotElement) holdingElement).getItem().addWindow(this);
                } else if (holdingElement instanceof SlotElement.InventorySlotElement) {
                    ((SlotElement.InventorySlotElement) holdingElement).getInventory().addWindow(this);
                }

                elementsDisplayed[index] = holdingElement;
            } else {
                elementsDisplayed[index] = null;
            }
        }
    }

    public void handleDragEvent(InventoryDragEvent event) {
        Player player = ((Player) event.getWhoClicked()).getPlayer();
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        Map<Integer, ItemStack> newItems = event.getNewItems();

        int itemsLeft = event.getCursor() == null ? 0 : event.getCursor().getAmount();
        for (int rawSlot : event.getRawSlots()) {
            ItemStack currentStack = event.getView().getItem(rawSlot);
            if (currentStack != null && currentStack.getType() == Material.AIR) currentStack = null;

            Pair<AbstractGui, Integer> pair = getGuiAt(rawSlot);
            if (pair != null && !pair.getFirst().handleItemDrag(updateReason, pair.getSecond(), currentStack, newItems.get(rawSlot))) {
                int currentAmount = currentStack == null ? 0 : currentStack.getAmount();
                int newAmount = newItems.get(rawSlot).getAmount();

                itemsLeft += newAmount - currentAmount;
            }
        }

        Do.sync(() -> event.getRawSlots().forEach(rawSlot -> {
            if (getGuiAt(rawSlot) != null) redrawItem(rawSlot);
        }));

        ItemStack cursorStack = event.getOldCursor();
        cursorStack.setAmount(itemsLeft);
        event.setCursor(cursorStack);
    }

    public void handleClickEvent(InventoryClickEvent event) {
        if (Arrays.asList(getInventories()).contains(event.getClickedInventory())) {
            handleClick(event);
        } else if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            if (outsideClickHandlers != null) {
                for (var handler : outsideClickHandlers) {
                    handler.accept(event);
                }
            }
        } else {
            switch (event.getAction()) {
                case MOVE_TO_OTHER_INVENTORY:
                    handleItemShift(event);
                    break;

                case COLLECT_TO_CURSOR:
                    handleCursorCollect(event);
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void handleCursorCollect(InventoryClickEvent event) {
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        ItemStack template = event.getCursor();

        List<Inventory> inventories = getContentInventories();
        Inventory inventory = new CompositeInventory(inventories);

        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        int amount = inventory.collectSimilar(updateReason, template);

        template.setAmount(amount);
        event.setCursor(template);
    }

    public void handleItemProviderUpdate(Item item) {
        getItemSlotElements(item).forEach((index, slotElement) ->
                redrawItem(index, slotElement, false));
    }

    public void handleInventoryUpdate(Inventory inventory) {
        getInvSlotElements(inventory).forEach((index, slotElement) ->
                redrawItem(index, slotElement, false));
    }

    protected Map<Integer, SlotElement> getItemSlotElements(Item item) {
        return ArrayUtils.findAllOccurrences(elementsDisplayed, element -> element instanceof SlotElement.ItemSlotElement
                && ((SlotElement.ItemSlotElement) element).getItem() == item);
    }

    protected Map<Integer, SlotElement> getInvSlotElements(Inventory inventory) {
        return ArrayUtils.findAllOccurrences(elementsDisplayed, element -> element instanceof SlotElement.InventorySlotElement
                && ((SlotElement.InventorySlotElement) element).getInventory() == inventory);
    }

    @Override
    public void open() {
        Player viewer = getViewer();
        if (currentlyOpen)
            throw new IllegalStateException("Window is already open");

        AbstractWindow openWindow = (AbstractWindow) WindowManager.getInstance().getOpenWindow(viewer);
        if (openWindow != null) {
            openWindow.handleCloseEvent(true);
        }

        currentlyOpen = true;
        hasHandledClose = false;
        initItems();
        WindowManager.getInstance().addWindow(this);
        for (AbstractGui gui : getGuis()) gui.addParent(this);
        openInventory(viewer);
    }

    protected void openInventory(@NotNull Player viewer) {
        NmsMultiVersion.getInventoryUtils().openCustomInventory(
                viewer,
                getInventories()[0],
                title
        );
    }

    public void handleOpenEvent(InventoryOpenEvent event) {
        if (!event.getPlayer().equals(getViewer())) {
            event.setCancelled(true);
        } else {
            handleOpened();

            if (openHandlers != null) {
                openHandlers.forEach(Runnable::run);
            }
        }
    }

    @Override
    public void close() {
        Player viewer = getCurrentViewer();
        if (viewer != null) {
            handleCloseEvent(true);
            viewer.closeInventory();
        }
    }

    public void handleCloseEvent(boolean forceClose) {
        if (hasHandledClose) return;

        if (closeable || forceClose) {
            if (!currentlyOpen) {
                throw new IllegalStateException("Window is already closed!");
            }

            closeable = true;
            currentlyOpen = false;
            hasHandledClose = true;

            remove();
            handleClosed();

            if (closeHandlers != null) {
                closeHandlers.forEach(Runnable::run);
            }
        } else {
            Do.sync(() -> openInventory(viewer));
        }
    }

    private void remove() {
        WindowManager.getInstance().removeWindow(this);

        Arrays.stream(elementsDisplayed)
                .filter(Objects::nonNull)
                .map(SlotElement::getHoldingElement)
                .forEach(slotElement -> {
                    if (slotElement instanceof SlotElement.ItemSlotElement) {
                        ((SlotElement.ItemSlotElement) slotElement).getItem().removeWindow(this);
                    } else if (slotElement instanceof SlotElement.InventorySlotElement) {
                        ((SlotElement.InventorySlotElement) slotElement).getInventory().removeWindow(this);
                    }
                });

        for (AbstractGui gui : getGuis()) {
            gui.removeParent(this);
        }
    }

    @Override
    public void changeTitle(@NotNull ComponentWrapper title) {
        this.title = title;
        Player currentViewer = getCurrentViewer();
        if (currentViewer != null) {
            NmsMultiVersion.getInventoryUtils().updateOpenInventoryTitle(currentViewer, title);
        }
    }

    @Override
    public void changeTitle(@NotNull BaseComponent[] title) {
        changeTitle(new BungeeComponentWrapper(title));
    }

    @Override
    public void changeTitle(@NotNull String title) {
        changeTitle(TextComponent.fromLegacyText(title));
    }

    @Override
    public void setOpenHandlers(@Nullable List<@NotNull Runnable> openHandlers) {
        this.openHandlers = openHandlers;
    }

    @Override
    public void addOpenHandler(@NotNull Runnable openHandler) {
        if (openHandlers == null)
            openHandlers = new ArrayList<>();

        openHandlers.add(openHandler);
    }

    @Override
    public void setCloseHandlers(@Nullable List<@NotNull Runnable> closeHandlers) {
        this.closeHandlers = closeHandlers;
    }

    @Override
    public void addCloseHandler(@NotNull Runnable closeHandler) {
        if (closeHandlers == null)
            closeHandlers = new ArrayList<>();

        closeHandlers.add(closeHandler);
    }

    @Override
    public void removeCloseHandler(@NotNull Runnable closeHandler) {
        if (closeHandlers != null)
            closeHandlers.remove(closeHandler);
    }

    @Override
    public void setOutsideClickHandlers(@Nullable List<@NotNull Consumer<@NotNull InventoryClickEvent>> outsideClickHandlers) {
        this.outsideClickHandlers = outsideClickHandlers;
    }

    @Override
    public void addOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler) {
        if (this.outsideClickHandlers == null)
            this.outsideClickHandlers = new ArrayList<>();

        this.outsideClickHandlers.add(outsideClickHandler);
    }

    @Override
    public void removeOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler) {
        if (this.outsideClickHandlers != null)
            this.outsideClickHandlers.remove(outsideClickHandler);
    }

    @Override
    public @Nullable Player getCurrentViewer() {
        List<HumanEntity> viewers = getInventories()[0].getViewers();
        return viewers.isEmpty() ? null : (Player) viewers.getFirst();
    }

    @Override
    public @NotNull Player getViewer() {
        return viewer;
    }

    @Override
    public @NotNull UUID getViewerUUID() {
        return viewerUUID;
    }

    @Override
    public boolean isCloseable() {
        return closeable;
    }

    @Override
    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    @Override
    public boolean isOpen() {
        return currentlyOpen;
    }

    protected abstract void setInvItem(int slot, ItemStack itemStack);

    protected abstract SlotElement getSlotElement(int index);

    protected abstract Pair<AbstractGui, Integer> getGuiAt(int index);

    protected abstract AbstractGui[] getGuis();

    protected abstract org.bukkit.inventory.Inventory[] getInventories();

    protected abstract List<Inventory> getContentInventories();

    protected abstract void initItems();

    protected abstract void handleOpened();

    protected abstract void handleClosed();

    protected abstract void handleClick(InventoryClickEvent event);

    protected abstract void handleItemShift(InventoryClickEvent event);

    public abstract void handleViewerDeath(PlayerDeathEvent event);

    @SuppressWarnings({"deprecation", "unchecked"})
    public static abstract class AbstractBuilder<W extends Window, S extends Window.Builder<W, S>> implements Window.Builder<W, S> {

        protected Player viewer;
        protected ComponentWrapper title;
        protected boolean closeable = true;
        protected List<Runnable> openHandlers;
        protected List<Runnable> closeHandlers;
        protected List<Consumer<InventoryClickEvent>> outsideClickHandlers;
        protected List<Consumer<W>> modifiers;

        @Override
        public @NotNull S setViewer(@NotNull Player viewer) {
            this.viewer = viewer;
            return (S) this;
        }

        @Override
        public @NotNull S setTitle(@NotNull ComponentWrapper title) {
            this.title = title;
            return (S) this;
        }

        @Override
        public @NotNull S setTitle(@NotNull BaseComponent @NotNull [] title) {
            this.title = new BungeeComponentWrapper(title);
            return (S) this;
        }

        @Override
        public @NotNull S setTitle(@NotNull String title) {
            this.title = new BungeeComponentWrapper(TextComponent.fromLegacyText(title));
            return (S) this;
        }

        @Override
        public @NotNull S setCloseable(boolean closeable) {
            this.closeable = closeable;
            return (S) this;
        }

        @Override
        public @NotNull S setOpenHandlers(@Nullable List<@NotNull Runnable> openHandlers) {
            this.openHandlers = openHandlers;
            return (S) this;
        }

        @Override
        public @NotNull S addOpenHandler(@NotNull Runnable openHandler) {
            if (openHandlers == null)
                openHandlers = new ArrayList<>();

            openHandlers.add(openHandler);
            return (S) this;
        }

        @Override
        public @NotNull S setCloseHandlers(@Nullable List<@NotNull Runnable> closeHandlers) {
            this.closeHandlers = closeHandlers;
            return (S) this;
        }

        @Override
        public @NotNull S addCloseHandler(@NotNull Runnable closeHandler) {
            if (closeHandlers == null)
                closeHandlers = new ArrayList<>();

            closeHandlers.add(closeHandler);
            return (S) this;
        }

        @Override
        public @NotNull S setOutsideClickHandlers(@NotNull List<@NotNull Consumer<@NotNull InventoryClickEvent>> outsideClickHandlers) {
            this.outsideClickHandlers = outsideClickHandlers;
            return (S) this;
        }

        @Override
        public @NotNull S addOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler) {
            if (outsideClickHandlers == null)
                outsideClickHandlers = new ArrayList<>();

            outsideClickHandlers.add(outsideClickHandler);
            return (S) this;
        }

        @Override
        public @NotNull S setModifiers(@Nullable List<@NotNull Consumer<@NotNull W>> modifiers) {
            this.modifiers = modifiers;
            return (S) this;
        }

        @Override
        public @NotNull S addModifier(@NotNull Consumer<@NotNull W> modifier) {
            if (modifiers == null)
                modifiers = new ArrayList<>();

            modifiers.add(modifier);
            return (S) this;
        }

        protected void applyModifiers(W window) {
            if (openHandlers != null)
                window.setOpenHandlers(openHandlers);

            if (closeHandlers != null)
                window.setCloseHandlers(closeHandlers);

            if (outsideClickHandlers != null)
                window.setOutsideClickHandlers(outsideClickHandlers);

            if (modifiers != null)
                modifiers.forEach(modifier -> modifier.accept(window));
        }

        @Override
        public @NotNull W build() {
            return build(viewer);
        }

        @Override
        public void open(Player viewer) {
            build(viewer).open();
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull S clone() {
            try {
                var clone = (AbstractBuilder<W, S>) super.clone();
                if (title != null)
                    clone.title = title.clone();
                if (closeHandlers != null)
                    clone.closeHandlers = new ArrayList<>(closeHandlers);
                if (modifiers != null)
                    clone.modifiers = new ArrayList<>(modifiers);
                return (S) clone;
            } catch (CloneNotSupportedException ignored) {
                throw new AssertionError();
            }
        }
    }
}