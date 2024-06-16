package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class PagedNestedGuiImpl extends AbstractPagedGui<Gui> {

    public PagedNestedGuiImpl(int width, int height, @Nullable List<@NotNull Gui> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }

    public PagedNestedGuiImpl(@Nullable List<@NotNull Gui> guis, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }

    @Override
    public void bake() {
        List<List<SlotElement>> pages = new ArrayList<>();
        for (Gui gui : content) {
            List<SlotElement> page = new ArrayList<>(gui.getSize());
            for (int slot = 0; slot < gui.getSize(); slot++) {
                page.add(new SlotElement.LinkedSlotElement(gui, slot));
            }

            pages.add(page);
        }

        this.pages = pages;
        update();
    }

    public static final class Builder extends AbstractBuilder<Gui> {

        @Override
        public @NotNull PagedGui<Gui> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");

            var gui = new PagedNestedGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
    }
}