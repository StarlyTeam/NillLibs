package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.gui.structure.Marker;
import kr.starly.libs.inventory.gui.structure.Structure;
import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.animation.Animation;
import kr.starly.libs.inventory.inventory.Inventory;
import kr.starly.libs.inventory.window.Window;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Gui {

    static @NotNull Builder.Normal normal() {
        return new NormalGuiImpl.Builder();
    }

    static @NotNull Gui normal(@NotNull Consumer<Builder.@NotNull Normal> consumer) {
        Builder.Normal builder = normal();
        consumer.accept(builder);
        return builder.build();
    }

    static @NotNull Gui empty(int width, int height) {
        return new NormalGuiImpl(width, height);
    }

    static @NotNull Gui of(@NotNull Structure structure) {
        return new NormalGuiImpl(structure);
    }

    int getSize();

    int getWidth();

    int getHeight();

    void setSlotElement(int x, int y, @Nullable SlotElement slotElement);

    void setSlotElement(int index, @Nullable SlotElement slotElement);

    void addSlotElements(@NotNull SlotElement... slotElements);

    @Nullable
    SlotElement getSlotElement(int x, int y);

    @Nullable
    SlotElement getSlotElement(int index);

    boolean hasSlotElement(int x, int y);

    boolean hasSlotElement(int index);

    @Nullable
    SlotElement @NotNull [] getSlotElements();

    void setItem(int x, int y, @Nullable Item item);

    void setItem(int index, @Nullable Item item);

    void addItems(@NotNull Item... items);

    @Nullable
    Item getItem(int x, int y);

    @Nullable
    Item getItem(int index);

    @Nullable
    ItemProvider getBackground();

    void setBackground(@Nullable ItemProvider itemProvider);

    void remove(int x, int y);

    void remove(int index);

    void applyStructure(@NotNull Structure structure);

    @NotNull List<@NotNull Window> findAllWindows();

    @NotNull Set<@NotNull Player> findAllCurrentViewers();

    void closeForAllViewers();

    void playAnimation(@NotNull Animation animation, @Nullable Predicate<@NotNull SlotElement> filter);

    void cancelAnimation();

    void setFrozen(boolean frozen);

    boolean isFrozen();

    void setIgnoreObscuredInventorySlots(boolean ignoreObscuredInventorySlots);

    boolean isIgnoreObscuredInventorySlots();

    void fill(int start, int end, @Nullable Item item, boolean replaceExisting);

    void fill(@Nullable Item item, boolean replaceExisting);

    void fillRow(int row, @Nullable Item item, boolean replaceExisting);

    void fillColumn(int column, @Nullable Item item, boolean replaceExisting);

    void fillBorders(@Nullable Item item, boolean replaceExisting);

    void fillRectangle(int x, int y, int width, int height, @Nullable Item item, boolean replaceExisting);

    void fillRectangle(int x, int y, @NotNull Gui gui, boolean replaceExisting);

    void fillRectangle(int x, int y, int width, @NotNull Inventory inventory, boolean replaceExisting);

    void fillRectangle(int x, int y, int width, @NotNull Inventory inventory, @Nullable ItemProvider background, boolean replaceExisting);

    interface Builder<G extends Gui, S extends Builder<G, S>> extends Cloneable {

        @NotNull     S setStructure(@NotNull Structure structure);

        @NotNull     S setStructure(@NotNull String... structureData);

        @NotNull     S setStructure(int width, int height, @NotNull String structureData);

        @NotNull     S addIngredient(char key, @NotNull ItemStack itemStack);

        @NotNull     S addIngredient(char key, @NotNull ItemProvider itemProvider);

        @NotNull     S addIngredient(char key, @NotNull Item item);

        @NotNull     S addIngredient(char key, @NotNull Inventory inventory);

        @NotNull     S addIngredient(char key, @NotNull Inventory inventory, @Nullable ItemProvider background);

        @NotNull     S addIngredient(char key, @NotNull SlotElement element);

        @NotNull     S addIngredient(char key, @NotNull Marker marker);

        @NotNull     S addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier);

        @NotNull     S addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier);

        @NotNull     S setBackground(@NotNull ItemProvider itemProvider);

        @NotNull     S setBackground(@NotNull ItemStack itemStack);

        @NotNull     S setFrozen(boolean frozen);

        @NotNull     S setIgnoreObscuredInventorySlots(boolean ignoreObscuredInventorySlots);

        @NotNull     S addModifier(@NotNull Consumer<@NotNull G> modifier);

        @NotNull     S setModifiers(@NotNull List<@NotNull Consumer<@NotNull G>> modifiers);

        @NotNull     G build();

        @NotNull     S clone();

        interface Normal extends Builder<Gui, Normal> {
        }
    }
}