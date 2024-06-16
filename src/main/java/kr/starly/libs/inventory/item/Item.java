package kr.starly.libs.inventory.item;

import kr.starly.libs.inventory.window.AbstractWindow;
import kr.starly.libs.inventory.window.Window;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Item {

    ItemProvider getItemProvider();

    void addWindow(AbstractWindow window);

    void removeWindow(AbstractWindow window);

    Set<Window> getWindows();

    void notifyWindows();

    void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event);
}