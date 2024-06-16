package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import kr.starly.libs.inventory.inventory.Inventory;
import kr.starly.libs.inventory.inventory.VirtualInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class PagedInventoriesGuiImpl extends AbstractPagedGui<Inventory> {

    private final @NotNull BiConsumer<@NotNull Integer, @NotNull Integer> resizeHandler = (from, to) -> bake();

    public PagedInventoriesGuiImpl(int width, int height, @Nullable List<@NotNull Inventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }

    public PagedInventoriesGuiImpl(@Nullable List<@NotNull Inventory> inventories, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }

    @Override
    public void setContent(@Nullable List<Inventory> content) {
        if (this.content != null) {
            for (Inventory inventory : this.content) {
                if (inventory instanceof VirtualInventory) {
                    ((VirtualInventory) inventory).removeResizeHandler(resizeHandler);
                }
            }
        }

        super.setContent(content);

        if (this.content != null) {
            for (Inventory inventory : this.content) {
                if (inventory instanceof VirtualInventory) {
                    ((VirtualInventory) inventory).addResizeHandler(resizeHandler);
                }
            }
        }
    }

    @Override
    public void bake() {
        int contentSize = getContentListSlots().length;

        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);

        for (Inventory inventory : content) {
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                page.add(new SlotElement.InventorySlotElement(inventory, slot));

                if (page.size() >= contentSize) {
                    pages.add(page);
                    page = new ArrayList<>(contentSize);
                }
            }
        }

        if (!page.isEmpty()) {
            pages.add(page);
        }

        this.pages = pages;
        update();
    }

    public static final class Builder extends AbstractBuilder<Inventory> {

        @Override
        public @NotNull PagedGui<Inventory> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");

            var gui = new PagedInventoriesGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
    }
}