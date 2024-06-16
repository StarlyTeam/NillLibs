package kr.starly.libs.inventory.item;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

@Getter
public class Click {

    private final Player player;
    private final ClickType clickType;
    private final InventoryClickEvent event;

    public Click(InventoryClickEvent event) {
        this.player = (Player) event.getWhoClicked();
        this.clickType = event.getClick();
        this.event = event;
    }
}