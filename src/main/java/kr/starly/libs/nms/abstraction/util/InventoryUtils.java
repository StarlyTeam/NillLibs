package kr.starly.libs.nms.abstraction.util;

import kr.starly.libs.nms.component.ComponentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InventoryUtils {

    void openCustomInventory(@NotNull Player player, @NotNull Inventory inventory);

    void openCustomInventory(@NotNull Player player, @NotNull Inventory inventory, @Nullable ComponentWrapper title);

    void updateOpenInventoryTitle(@NotNull Player player, @NotNull ComponentWrapper title);
}