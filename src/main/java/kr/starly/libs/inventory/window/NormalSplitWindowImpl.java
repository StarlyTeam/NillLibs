package kr.starly.libs.inventory.window;

import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.inventory.gui.AbstractGui;
import kr.starly.libs.util.InventoryUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NormalSplitWindowImpl extends AbstractSplitWindow {

    public NormalSplitWindowImpl(
            @NotNull Player player,
            @Nullable ComponentWrapper title,
            @NotNull AbstractGui upperGui,
            @NotNull AbstractGui lowerGui,
            boolean closeable
    ) {
        super(player, title, upperGui, lowerGui, InventoryUtils.createMatchingInventory(upperGui, ""), closeable);
    }

    public static final class BuilderImpl
            extends AbstractSplitWindow.AbstractBuilder<Window, Window.Builder.Normal.Split>
            implements Window.Builder.Normal.Split {

        @Override
        public @NotNull Window build(Player viewer) {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (upperGuiSupplier == null)
                throw new IllegalStateException("Upper Gui is not defined.");
            if (lowerGuiSupplier == null)
                throw new IllegalStateException("Lower Gui is not defined.");

            var window = new NormalSplitWindowImpl(
                    viewer,
                    title,
                    (AbstractGui) upperGuiSupplier.get(),
                    (AbstractGui) lowerGuiSupplier.get(),
                    closeable
            );

            applyModifiers(window);
            return window;
        }
    }
}