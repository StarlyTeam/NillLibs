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

final class AnvilSingleWindowImpl extends AbstractSingleWindow implements AnvilWindow {

    private final AnvilInventory anvilInventory;

    public AnvilSingleWindowImpl(
            @NotNull Player player,
            @Nullable ComponentWrapper title,
            @NotNull AbstractGui gui,
            @Nullable List<@NotNull Consumer<@NotNull String>> renameHandlers,
            boolean closable
    ) {
        super(player, title, gui, null, closable);
        anvilInventory = NmsMultiVersion.createAnvilInventory(player, title == null ? BungeeComponentWrapper.EMPTY : title, renameHandlers);
        inventory = anvilInventory.getBukkitInventory();
    }

    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
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
            extends AbstractSingleWindow.AbstractBuilder<AnvilWindow, AnvilWindow.Builder.Single>
            implements AnvilWindow.Builder.Single {

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
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");

            var window = new AnvilSingleWindowImpl(
                    viewer,
                    title,
                    (AbstractGui) guiSupplier.get(),
                    renameHandlers,
                    closeable
            );

            applyModifiers(window);
            return window;
        }
    }
}