package kr.starly.libs.inventory.window;

import kr.starly.libs.nms.component.ComponentWrapper;
import kr.starly.libs.inventory.gui.Gui;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Window {

    static @NotNull Builder.Normal.Single single() {
        return new NormalSingleWindowImpl.BuilderImpl();
    }

    static @NotNull Window single(@NotNull Consumer<Builder.Normal.@NotNull Single> consumer) {
        Builder.Normal.Single builder = single();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull Builder.Normal.Split split() {
        return new NormalSplitWindowImpl.BuilderImpl();
    }

    static @NotNull Window split(@NotNull Consumer<Builder.Normal.@NotNull Split> consumer) {
        Builder.Normal.Split builder = split();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull Builder.Normal.Merged merged() {
        return new NormalMergedWindowImpl.BuilderImpl();
    }

    static @NotNull Window merged(@NotNull Consumer<Builder.Normal.@NotNull Merged> consumer) {
        Builder.Normal.Merged builder = merged();
        consumer.accept(builder);
        return builder.build();
    }

    void open();

    boolean isCloseable();

    void setCloseable(boolean closeable);

    void close();

    boolean isOpen();

    void changeTitle(@NotNull ComponentWrapper title);

    void changeTitle(@NotNull BaseComponent[] title);

    void changeTitle(@NotNull String title);

    @Nullable
    Player getViewer();

    @Nullable
    Player getCurrentViewer();

    @NotNull UUID getViewerUUID();

    @Nullable
    ItemStack @Nullable [] getPlayerItems();

    void setOpenHandlers(@Nullable List<@NotNull Runnable> openHandlers);

    void addOpenHandler(@NotNull Runnable openHandler);

    void setCloseHandlers(@Nullable List<@NotNull Runnable> closeHandlers);

    void addCloseHandler(@NotNull Runnable closeHandler);

    void removeCloseHandler(@NotNull Runnable closeHandler);

    void setOutsideClickHandlers(@Nullable List<@NotNull Consumer<@NotNull InventoryClickEvent>> outsideClickHandlers);

    void addOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler);

    void removeOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler);

    interface Builder<W extends Window, S extends Builder<W, S>> extends Cloneable {

        @NotNull     S setViewer(@NotNull Player viewer);

        @NotNull     S setTitle(@NotNull ComponentWrapper title);

        @NotNull     S setTitle(@NotNull BaseComponent @NotNull [] title);

        @NotNull     S setTitle(@NotNull String title);

        @NotNull     S setCloseable(boolean closeable);

        @NotNull     S setOpenHandlers(@Nullable List<@NotNull Runnable> openHandlers);

        @NotNull     S addOpenHandler(@NotNull Runnable openHandler);

        @NotNull     S setCloseHandlers(@Nullable List<@NotNull Runnable> closeHandlers);

        @NotNull     S addCloseHandler(@NotNull Runnable closeHandler);

        @NotNull     S setOutsideClickHandlers(@NotNull List<@NotNull Consumer<@NotNull InventoryClickEvent>> outsideClickHandlers);

        @NotNull     S addOutsideClickHandler(@NotNull Consumer<@NotNull InventoryClickEvent> outsideClickHandler);

        @NotNull     S setModifiers(@Nullable List<@NotNull Consumer<@NotNull W>> modifiers);

        @NotNull     S addModifier(@NotNull Consumer<@NotNull W> modifier);

        @NotNull     W build();

        @NotNull     W build(Player viewer);

        void open(Player viewer);

        @NotNull     S clone();

        interface Single<W extends Window, S extends Single<W, S>> extends Builder<W, S> {

            @NotNull         S setGui(@NotNull Gui gui);

            @NotNull         S setGui(@NotNull Gui.Builder<?, ?> builder);

            @NotNull         S setGui(@NotNull Supplier<Gui> guiSupplier);

        }

        interface Double<W extends Window, S extends Double<W, S>> extends Builder<W, S> {

            @NotNull         S setUpperGui(@NotNull Gui gui);

            @NotNull         S setUpperGui(@NotNull Gui.Builder<?, ?> builder);

            @NotNull         S setUpperGui(@NotNull Supplier<Gui> guiSupplier);

            @NotNull         S setLowerGui(@NotNull Gui gui);

            @NotNull         S setLowerGui(@NotNull Gui.Builder<?, ?> builder);

            @NotNull         S setLowerGui(@NotNull Supplier<Gui> guiSupplier);
        }

        interface Normal<V, S extends Normal<V, S>> extends Builder<Window, S> {

            interface Single extends Normal<UUID, Single>, Builder.Single<Window, Single> {
            }

            interface Split extends Normal<Player, Split>, Double<Window, Split> {
            }

            interface Merged extends Normal<Player, Merged>, Builder.Single<Window, Merged> {
            }
        }
    }
}