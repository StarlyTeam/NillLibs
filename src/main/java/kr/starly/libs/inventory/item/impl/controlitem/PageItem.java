package kr.starly.libs.inventory.item.impl.controlitem;

import kr.starly.libs.inventory.gui.PagedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public abstract class PageItem extends ControlItem<PagedGui<?>> {

    private final boolean forward;

    public PageItem(boolean forward) {
        this.forward = forward;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType == ClickType.LEFT) {
            if (forward) getGui().goForward();
            else getGui().goBack();
        }
    }
}