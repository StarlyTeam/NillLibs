package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import kr.starly.libs.inventory.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class PagedItemsGuiImpl extends AbstractPagedGui<Item> {

    public PagedItemsGuiImpl(int width, int height, @Nullable List<@NotNull Item> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }

    public PagedItemsGuiImpl(@Nullable List<@NotNull Item> items, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }

    @Override
    public void bake() {
        int contentSize = getContentListSlots().length;

        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);

        for (Item item : content) {
            page.add(new SlotElement.ItemSlotElement(item));

            if (page.size() >= contentSize) {
                pages.add(page);
                page = new ArrayList<>(contentSize);
            }
        }

        if (!page.isEmpty()) {
            pages.add(page);
        }

        this.pages = pages;
        update();
    }

    public static final class Builder extends AbstractBuilder<Item> {

        @Override
        public @NotNull PagedGui<Item> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");

            var gui = new PagedItemsGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
    }
}