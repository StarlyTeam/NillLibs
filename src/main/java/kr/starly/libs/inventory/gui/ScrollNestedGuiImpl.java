package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class ScrollNestedGuiImpl extends AbstractScrollGui<Gui> {

    public ScrollNestedGuiImpl(int width, int height, @Nullable List<@NotNull Gui> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }

    public ScrollNestedGuiImpl(@Nullable List<@NotNull Gui> guis, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }

    @Override
    public void bake() {
        ArrayList<SlotElement> elements = new ArrayList<>();
        for (Gui gui : content) {
            for (int i = 0; i < gui.getSize(); i++) {
                elements.add(new SlotElement.LinkedSlotElement(gui, i));
            }
        }

        this.elements = elements;
        update();
    }

    public static final class Builder extends AbstractBuilder<Gui> {

        @Override
        public @NotNull ScrollGui<Gui> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");

            var gui = new ScrollNestedGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
    }
}