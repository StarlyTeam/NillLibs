package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;

final class NormalGuiImpl extends AbstractGui {

    public NormalGuiImpl(int width, int height) {
        super(width, height);
    }

    public NormalGuiImpl(@NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight());
        applyStructure(structure);
    }

    public static class Builder extends AbstractBuilder<Gui, Gui.Builder.Normal> implements Gui.Builder.Normal {

        @Override
        public @NotNull Gui build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");

            var gui = new NormalGuiImpl(structure);
            applyModifiers(gui);
            return gui;
        }
    }
}