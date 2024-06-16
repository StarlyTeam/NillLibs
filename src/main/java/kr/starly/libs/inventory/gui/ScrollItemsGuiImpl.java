package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import kr.starly.libs.inventory.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class ScrollItemsGuiImpl extends AbstractScrollGui<Item> {

    public ScrollItemsGuiImpl(int width, int height, @Nullable List<@NotNull Item> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }

    public ScrollItemsGuiImpl(@Nullable List<@NotNull Item> items, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }

    @Override
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>(content.size());
        for (Item item : content) {
            elements.add(new SlotElement.ItemSlotElement(item));
        }

        this.elements = elements;
        update();
    }

    public static final class Builder extends AbstractBuilder<Item> {

        @Override
        public @NotNull ScrollGui<Item> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");

            var gui = new ScrollItemsGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
    }
}