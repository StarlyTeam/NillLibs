package kr.starly.libs.inventory.item.impl;

import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.window.AbstractWindow;
import kr.starly.libs.scheduler.Do;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class AutoCycleItem extends AbstractItem {

    private final ItemProvider[] itemProviders;
    private final int period;
    private BukkitTask task;

    private int state;

    public AutoCycleItem(int period, ItemProvider... itemProviders) {
        this.itemProviders = itemProviders;
        this.period = period;
    }

    public void start() {
        if (task != null) task.cancel();
        task = Do.syncTimer(period, this::cycle);
    }

    public void cancel() {
        task.cancel();
        task = null;
    }

    private void cycle() {
        state++;
        if (state == itemProviders.length) state = 0;
        notifyWindows();
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemProviders[state];
    }

    @Override
    public void addWindow(AbstractWindow window) {
        super.addWindow(window);
        if (task == null) start();
    }

    @Override
    public void removeWindow(AbstractWindow window) {
        super.removeWindow(window);
        if (getWindows().isEmpty() && task != null) cancel();
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
    }
}