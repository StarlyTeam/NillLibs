package kr.starly.libs.inventory.gui.structure;

import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.item.ItemWrapper;
import kr.starly.libs.inventory.gui.SlotElement;
import kr.starly.libs.inventory.gui.SlotElement.ItemSlotElement;
import kr.starly.libs.inventory.inventory.Inventory;
import kr.starly.libs.inventory.item.impl.SimpleItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

public class Structure implements Cloneable {

    private static final HashMap<Character, Ingredient> globalIngredientMap = new HashMap<>();

    private final String structureData;
    @Getter
    private final int width;
    @Getter
    private final int height;

    private HashMap<Character, Ingredient> ingredientMap = new HashMap<>();
    private IngredientList ingredientList;

    public Structure(@NotNull String @NotNull ... structureData) {
        this(sanitize(structureData[0]).length(), structureData.length, String.join("", structureData));
    }

    public Structure(int width, int height, @NotNull String structureData) {
        this.width = width;
        this.height = height;
        this.structureData = sanitize(structureData);

        if (width * height != this.structureData.length())
            throw new IllegalArgumentException("Length of structure data does not match width * height");
    }

    private static String sanitize(String s) {
        return s.replace(" ", "").replace("\n", "");
    }

    public static void addGlobalIngredient(char key, @NotNull ItemStack itemStack) {
        addGlobalIngredient(key, new ItemWrapper(itemStack));
    }

    public static void addGlobalIngredient(char key, @NotNull ItemProvider itemProvider) {
        addGlobalIngredient(key, new SimpleItem(itemProvider));
    }

    public static void addGlobalIngredient(char key, @NotNull Item item) {
        addGlobalIngredient(key, new ItemSlotElement(item));
    }

    public static void addGlobalIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        addGlobalIngredientElementSupplier(key, () -> new ItemSlotElement(itemSupplier.get()));
    }

    public static void addGlobalIngredient(char key, @NotNull SlotElement element) {
        globalIngredientMap.put(key, new Ingredient(element));
    }

    public static void addGlobalIngredient(char key, @NotNull Marker marker) {
        globalIngredientMap.put(key, new Ingredient(marker));
    }

    public static void addGlobalIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        globalIngredientMap.put(key, new Ingredient(elementSupplier));
    }

    public @NotNull Structure addIngredient(char key, @NotNull ItemStack itemStack) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new ItemWrapper(itemStack));
    }

    public @NotNull Structure addIngredient(char key, @NotNull ItemProvider itemProvider) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new SimpleItem(itemProvider));
    }

    public @NotNull Structure addIngredient(char key, @NotNull Item item) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredient(key, new ItemSlotElement(item));
    }

    public @NotNull Structure addIngredient(char key, @NotNull Inventory inventory) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredientElementSupplier(key, new InventorySlotElementSupplier(inventory));
    }

    public @NotNull Structure addIngredient(char key, @NotNull Inventory inventory, @Nullable ItemProvider background) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        return addIngredientElementSupplier(key, new InventorySlotElementSupplier(inventory, background));
    }

    public @NotNull Structure addIngredient(char key, @NotNull SlotElement element) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(element));
        return this;
    }

    public @NotNull Structure addIngredient(char key, @NotNull Marker marker) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(marker));
        return this;
    }

    public @NotNull Structure addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(() -> new ItemSlotElement(itemSupplier.get())));
        return this;
    }

    public @NotNull Structure addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        if (ingredientList != null) throw new IllegalStateException("Structure is locked");
        ingredientMap.put(key, new Ingredient(elementSupplier));
        return this;
    }

    public @NotNull IngredientList getIngredientList() {
        if (ingredientList != null) return ingredientList;

        HashMap<Character, Ingredient> ingredients = new HashMap<>(globalIngredientMap);
        ingredients.putAll(this.ingredientMap);
        return ingredientList = new IngredientList(width, height, structureData, ingredients);
    }

    @Override
    public @NotNull Structure clone() {
        try {
            Structure clone = (Structure) super.clone();
            clone.ingredientMap = new HashMap<>(ingredientMap);
            clone.ingredientList = null;
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}