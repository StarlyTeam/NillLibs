package kr.starly.libs.inventory.item.impl.controlitem;

import kr.starly.libs.inventory.gui.TabGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public abstract class TabItem extends ControlItem<TabGui> {

    private final int tab;

    public TabItem(int tab) {
        this.tab = tab;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) getGui().setTab(tab);
    }
}