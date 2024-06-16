package kr.starly.libs.inventory.gui.structure;

import kr.starly.libs.inventory.gui.SlotElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Ingredient {

    private final SlotElement slotElement;
    private final Marker marker;
    private final Supplier<? extends SlotElement> elementSupplier;

    public Ingredient(@NotNull SlotElement slotElement) {
        this.slotElement = slotElement;
        this.elementSupplier = null;
        this.marker = null;
    }

    public Ingredient(@NotNull Supplier<? extends SlotElement> elementSupplier) {
        this.elementSupplier = elementSupplier;
        this.slotElement = null;
        this.marker = null;
    }

    public Ingredient(@NotNull Marker marker) {
        this.marker = marker;
        this.slotElement = null;
        this.elementSupplier = null;
    }

    public @Nullable SlotElement getSlotElement() {
        return slotElement == null ? elementSupplier.get() : slotElement;
    }

    public @Nullable Marker getMarker() {
        return marker;
    }

    public boolean isSlotElement() {
        return slotElement != null || elementSupplier != null;
    }

    public boolean isMarker() {
        return marker != null;
    }
}