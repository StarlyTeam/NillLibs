package kr.starly.libs.nms.abstraction.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AnvilInventory {

    @NotNull Inventory getBukkitInventory();

    void open();

    void setItem(int slot, @Nullable ItemStack itemStack);

    String getRenameText();

    boolean isOpen();
}