package kr.starly.libs.inventory.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;

@AllArgsConstructor
@Getter
public class PlayerUpdateReason implements UpdateReason {

    private final Player player;
    private final InventoryEvent event;
}