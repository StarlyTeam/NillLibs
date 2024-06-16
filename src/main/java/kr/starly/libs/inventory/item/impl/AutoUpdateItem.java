package kr.starly.libs.inventory.item.impl;

import kr.starly.libs.inventory.item.ItemProvider;
import kr.starly.libs.inventory.window.AbstractWindow;
import kr.starly.libs.scheduler.Do;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Supplier;

public class AutoUpdateItem extends SuppliedItem {

    private final int period;
    private BukkitTask task;

    public AutoUpdateItem(int period, Supplier<? extends ItemProvider> builderSupplier) {
        super(builderSupplier, null);
        this.period = period;
    }

    public void start() {
        if (task != null) task.cancel();
        task = Do.syncTimer(period, this::notifyWindows);
    }

    public void cancel() {
        task.cancel();
        task = null;
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
}