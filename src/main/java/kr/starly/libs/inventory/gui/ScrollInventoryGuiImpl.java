package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import kr.starly.libs.inventory.inventory.Inventory;
import kr.starly.libs.inventory.inventory.VirtualInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class ScrollInventoryGuiImpl extends AbstractScrollGui<Inventory> {

    private final @NotNull BiConsumer<@NotNull Integer, @NotNull Integer> resizeHandler = (from, to) -> bake();

    public ScrollInventoryGuiImpl(int width, int height, @Nullable List<@NotNull Inventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }

    public ScrollInventoryGuiImpl(@Nullable List<@NotNull Inventory> inventories, @NotNull Structure structure) {
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
        List<SlotElement> elements = new ArrayList<>();
        for (Inventory inventory : content) {
            for (int i = 0; i < inventory.getSize(); i++) {
                elements.add(new SlotElement.InventorySlotElement(inventory, i));
            }
        }

        this.elements = elements;
        update();
    }

    public static final class Builder extends AbstractBuilder<Inventory> {

        @Override
        public @NotNull ScrollGui<Inventory> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");

            var gui = new ScrollInventoryGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
    }
}