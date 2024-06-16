package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ScrollGui<C> extends Gui {

    static @NotNull Builder<@NotNull Item> items() {
        return new ScrollItemsGuiImpl.Builder();
    }

    static @NotNull ScrollGui<@NotNull Item> items(@NotNull Consumer<@NotNull Builder<@NotNull Item>> consumer) {
        Builder<Item> builder = items();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull ScrollGui<@NotNull Item> ofItems(int width, int height, @NotNull List<@NotNull Item> items, int... contentListSlots) {
        return new ScrollItemsGuiImpl(width, height, items, contentListSlots);
    }

    static @NotNull ScrollGui<@NotNull Item> ofItems(@NotNull Structure structure, @NotNull List<@NotNull Item> items) {
        return new ScrollItemsGuiImpl(items, structure);
    }

    static @NotNull Builder<@NotNull Gui> guis() {
        return new ScrollNestedGuiImpl.Builder();
    }

    static @NotNull ScrollGui<@NotNull Gui> guis(@NotNull Consumer<@NotNull Builder<@NotNull Gui>> consumer) {
        Builder<Gui> builder = guis();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull ScrollGui<@NotNull Gui> ofGuis(int width, int height, @NotNull List<@NotNull Gui> guis, int... contentListSlots) {
        return new ScrollNestedGuiImpl(width, height, guis, contentListSlots);
    }

    static @NotNull ScrollGui<@NotNull Gui> ofGuis(Structure structure, @NotNull List<@NotNull Gui> guis) {
        return new ScrollNestedGuiImpl(guis, structure);
    }

    static @NotNull Builder<@NotNull Inventory> inventories() {
        return new ScrollInventoryGuiImpl.Builder();
    }

    static @NotNull ScrollGui<@NotNull Inventory> inventories(@NotNull Consumer<@NotNull Builder<@NotNull Inventory>> consumer) {
        Builder<Inventory> builder = inventories();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull ScrollGui<@NotNull Inventory> ofInventories(int width, int height, @NotNull List<@NotNull Inventory> inventories, int... contentListSlots) {
        return new ScrollInventoryGuiImpl(width, height, inventories, contentListSlots);
    }

    static @NotNull ScrollGui<@NotNull Inventory> ofInventories(@NotNull Structure structure, @NotNull List<@NotNull Inventory> inventories) {
        return new ScrollInventoryGuiImpl(inventories, structure);
    }

    int getCurrentLine();

    int getMaxLine();

    void setCurrentLine(int line);

    boolean canScroll(int lines);

    void scroll(int lines);

    void setContent(@Nullable List<@NotNull C> content);

    void bake();

    void setScrollHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> scrollHandlers);

    void addScrollHandler(@NotNull BiConsumer<Integer, Integer> scrollHandler);

    void removeScrollHandler(@NotNull BiConsumer<Integer, Integer> scrollHandler);

    interface Builder<C> extends Gui.Builder<ScrollGui<C>, Builder<C>> {

        @NotNull     Builder<C> setContent(@NotNull List<@NotNull C> content);

        @NotNull     Builder<C> addContent(@NotNull C content);

        @NotNull     Builder<C> setScrollHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers);

        @NotNull     Builder<C> addScrollHandler(@NotNull BiConsumer<Integer, Integer> handler);
    }
}