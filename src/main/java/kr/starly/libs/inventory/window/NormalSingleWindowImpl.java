package kr.starly.libs.inventory.window;

import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.inventory.gui.AbstractGui;
import kr.starly.libs.util.InventoryUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NormalSingleWindowImpl extends AbstractSingleWindow {

    public NormalSingleWindowImpl(
            @NotNull Player player,
            @Nullable ComponentWrapper title,
            @NotNull AbstractGui gui,
            boolean closeable
    ) {
        super(player, title, gui, InventoryUtils.createMatchingInventory(gui, ""), closeable);
    }

    public static final class BuilderImpl
            extends AbstractSingleWindow.AbstractBuilder<Window, Window.Builder.Normal.Single>
            implements Window.Builder.Normal.Single {

        @Override
        public @NotNull Window build(Player viewer) {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");

            var window = new NormalSingleWindowImpl(
                    viewer,
                    title,
                    (AbstractGui) guiSupplier.get(),
                    closeable
            );

            applyModifiers(window);
            return window;
        }
    }
}