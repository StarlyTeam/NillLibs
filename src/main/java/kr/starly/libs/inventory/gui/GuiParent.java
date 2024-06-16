package kr.starly.libs.inventory.gui;

public interface GuiParent {

    void handleSlotElementUpdate(Gui child, int slotIndex);
}