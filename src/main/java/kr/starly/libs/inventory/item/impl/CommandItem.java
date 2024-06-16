package kr.starly.libs.inventory.item.impl;

import kr.starly.libs.inventory.item.ItemProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class CommandItem extends SimpleItem {

    private final String command;

    public CommandItem(@NotNull ItemProvider itemProvider, @NotNull String command) {
        super(itemProvider);
        this.command = command;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        player.chat(command);
    }
}