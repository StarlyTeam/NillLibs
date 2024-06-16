package kr.starly.libs.nms.abstraction.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface CartographyInventory {

    Inventory getBukkitInventory();

    void setItem(int slot, ItemStack itemStack);

    void open();

    boolean isOpen();
}