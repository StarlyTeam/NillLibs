package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface TabGui extends Gui {

    static @NotNull Builder normal() {
        return new TabGuiImpl.BuilderImpl();
    }

    static @NotNull TabGui normal(@NotNull Consumer<@NotNull Builder> consumer) {
        Builder builder = normal();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull TabGui of(int width, int height, @NotNull List<@Nullable Gui> tabs, int... contentListSlots) {
        return new TabGuiImpl(width, height, tabs, contentListSlots);
    }

    static @NotNull TabGui of(Structure structure, @NotNull List<@Nullable Gui> tabs) {
        return new TabGuiImpl(tabs, structure);
    }

    int getCurrentTab();

    void setTab(int tab);

    boolean isTabAvailable(int tab);

    @NotNull List<@Nullable Gui> getTabs();

    @Nullable
    List<@NotNull BiConsumer<Integer, Integer>> getTabChangeHandlers();

    void setTabChangeHandlers(@Nullable List<@NotNull BiConsumer<Integer, Integer>> handlers);

    void addTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);

    void removeTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);

    interface Builder extends Gui.Builder<TabGui, Builder> {

        @NotNull     Builder setTabs(@NotNull List<@Nullable Gui> tabs);

        @NotNull     Builder addTab(@Nullable Gui tab);

        @NotNull     Builder setTabChangeHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers);

        @NotNull     Builder addTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler);
    }
}