package kr.starly.libs.inventory.window;

import kr.starly.libs.nms.NmsMultiVersion;
import kr.starly.libs.nms.abstraction.inventory.AnvilInventory;
import kr.starly.libs.nms.component.BungeeComponentWrapper;
import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.inventory.gui.AbstractGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class AnvilSplitWindowImpl extends AbstractSplitWindow implements AnvilWindow {

    private final AnvilInventory anvilInventory;

    public AnvilSplitWindowImpl(
            @NotNull Player player,
            @Nullable ComponentWrapper title,
            @NotNull AbstractGui upperGui,
            @NotNull AbstractGui lowerGui,
            @Nullable List<@NotNull Consumer<@NotNull String>> renameHandlers,
            boolean closeable
    ) {
        super(player, title, upperGui, lowerGui, null, closeable);

        anvilInventory = NmsMultiVersion.createAnvilInventory(player, title == null ? BungeeComponentWrapper.EMPTY : title, renameHandlers);
        upperInventory = anvilInventory.getBukkitInventory();
    }

    @Override
    protected void setUpperInvItem(int slot, ItemStack itemStack) {
        anvilInventory.setItem(slot, itemStack);
    }

    @Override
    protected void openInventory(@NotNull Player viewer) {
        anvilInventory.open();
    }

    @Override
    public String getRenameText() {
        return anvilInventory.getRenameText();
    }

    public static final class BuilderImpl
            extends AbstractSplitWindow.AbstractBuilder<AnvilWindow, AnvilWindow.Builder.Split>
            implements AnvilWindow.Builder.Split {

        private List<Consumer<String>> renameHandlers;

        @Override
        public @NotNull BuilderImpl setRenameHandlers(@NotNull List<@NotNull Consumer<String>> renameHandlers) {
            this.renameHandlers = renameHandlers;
            return this;
        }

        @Override
        public @NotNull BuilderImpl addRenameHandler(@NotNull Consumer<String> renameHandler) {
            if (renameHandlers == null)
                renameHandlers = new ArrayList<>();

            renameHandlers.add(renameHandler);
            return this;
        }

        @Override
        public @NotNull AnvilWindow build(Player viewer) {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (upperGuiSupplier == null)
                throw new IllegalStateException("Upper Gui is not defined.");
            if (lowerGuiSupplier == null)
                throw new IllegalStateException("Lower Gui is not defined.");

            var window = new AnvilSplitWindowImpl(
                    viewer,
                    title,
                    (AbstractGui) upperGuiSupplier.get(),
                    (AbstractGui) lowerGuiSupplier.get(),
                    renameHandlers,
                    closeable
            );

            applyModifiers(window);
            return window;
        }
    }
}