package kr.starly.libs.inventory.gui;

import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.inventory.Inventory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface SlotElement {

    ItemStack getItemStack();

    SlotElement getHoldingElement();

    @AllArgsConstructor
    @Getter
    class ItemSlotElement implements SlotElement {

        private final Item item;

        @Override
        public ItemStack getItemStack() {
            return item.getItemProvider().get();
        }

        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
    }

    @AllArgsConstructor
    @Getter
    class InventorySlotElement implements SlotElement {

        private final Inventory inventory;
        private final int slot;
        private final ItemProvider background;

        public InventorySlotElement(Inventory inventory, int slot) {
            this.inventory = inventory;
            this.slot = slot;
            this.background = null;
        }

        @Override
        public ItemStack getItemStack() {
            ItemStack itemStack = inventory.getUnsafeItem(slot);
            if (itemStack == null && background != null) itemStack = background.get();
            return itemStack;
        }

        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
    }

    @Getter()
    class LinkedSlotElement implements SlotElement {

        private final Gui gui;
        private final int slotIndex;

        public LinkedSlotElement(Gui gui, int slot) {
            if (!(gui instanceof AbstractGui))
                throw new IllegalArgumentException("Illegal Gui implementation");

            this.gui = gui;
            this.slotIndex = slot;
        }

        @Override
        public SlotElement getHoldingElement() {
            LinkedSlotElement element = this;
            while (true) {
                SlotElement below = element.getGui().getSlotElement(element.getSlotIndex());
                if (below instanceof LinkedSlotElement) element = (LinkedSlotElement) below;
                else return below;
            }
        }

        public List<Gui> getGuiList() {
            ArrayList<Gui> guis = new ArrayList<>();
            LinkedSlotElement element = this;
            while (true) {
                guis.add(element.getGui());
                SlotElement below = element.getGui().getSlotElement(element.getSlotIndex());
                if (below instanceof LinkedSlotElement)
                    element = (LinkedSlotElement) below;
                else break;
            }

            return guis;
        }

        @Override
        public ItemStack getItemStack() {
            SlotElement holdingElement = getHoldingElement();
            return holdingElement != null ? holdingElement.getItemStack() : null;
        }
    }
}