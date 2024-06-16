package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface PagedGui<C> extends Gui {

    static @NotNull Builder<@NotNull Item> items() {
        return new PagedItemsGuiImpl.Builder();
    }

    static @NotNull PagedGui<@NotNull Item> items(@NotNull Consumer<@NotNull Builder<@NotNull Item>> consumer) {
        Builder<Item> builder = items();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull PagedGui<@NotNull Item> ofItems(int width, int height, @NotNull List<@NotNull Item> items, int... contentListSlots) {
        return new PagedItemsGuiImpl(width, height, items, contentListSlots);
    }

    static @NotNull PagedGui<@NotNull Item> ofItems(@NotNull Structure structure, @NotNull List<@NotNull Item> items) {
        return new PagedItemsGuiImpl(items, structure);
    }

    static @NotNull Builder<@NotNull Gui> guis() {
        return new PagedNestedGuiImpl.Builder();
    }

    static @NotNull PagedGui<@NotNull Gui> guis(@NotNull Consumer<@NotNull Builder<@NotNull Gui>> consumer) {
        Builder<Gui> builder = guis();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull PagedGui<@NotNull Gui> ofGuis(int width, int height, @NotNull List<@NotNull Gui> guis, int... contentListSlots) {
        return new PagedNestedGuiImpl(width, height, guis, contentListSlots);
    }

    static @NotNull PagedGui<@NotNull Gui> ofGuis(@NotNull Structure structure, @NotNull List<@NotNull Gui> guis) {
        return new PagedNestedGuiImpl(guis, structure);
    }

    static @NotNull Builder<@NotNull Inventory> inventories() {
        return new PagedInventoriesGuiImpl.Builder();
    }

    static @NotNull PagedGui<@NotNull Inventory> inventories(@NotNull Consumer<@NotNull Builder<@NotNull Inventory>> consumer) {
        Builder<Inventory> builder = inventories();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull PagedGui<@NotNull Inventory> ofInventories(int width, int height, @NotNull List<@NotNull Inventory> inventories, int... contentListSlots) {
        return new PagedInventoriesGuiImpl(width, height, inventories, contentListSlots);
    }

    static @NotNull PagedGui<@NotNull Inventory> ofInventories(@NotNull Structure structure, @NotNull List<@NotNull Inventory> inventories) {
        return new PagedInventoriesGuiImpl(inventories, structure);
    }

    int getPageAmount();

    int getCurrentPage();

    void setPage(int page);

    boolean hasNextPage();

    boolean hasPreviousPage();

    boolean hasInfinitePages();

    void goForward();

    void goBack();

    int[] getContentListSlots();

    @Nullable
    List<@NotNull C> getContent();

    void setContent(@Nullable List<@NotNull C> content);

    void bake();

    @Nullable
    List<@NotNull BiConsumer<Integer, Integer>> getPageChangeHandlers();

    void setPageChangeHandlers(@Nullable List<@NotNull BiConsumer<Integer, Integer>> handlers);

    void addPageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);

    void removePageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);

    interface Builder<C> extends Gui.Builder<PagedGui<C>, Builder<C>> {

        @NotNull     Builder<C> setContent(@NotNull List<@NotNull C> content);

        @NotNull     Builder<C> addContent(@NotNull C content);

        @NotNull     Builder<C> setPageChangeHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers);

        @NotNull     Builder<C> addPageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    }
}