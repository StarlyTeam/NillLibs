package kr.starly.libs.inventory;

import kr.starly.libs.NillLibs;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Getter
public class InventoryFramework implements Listener {

    private static InventoryFramework instance;

    private final List<Runnable> disableHandlers = new ArrayList<>();
    private final Plugin plugin;

    private InventoryFramework() {
        this.plugin = NillLibs.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static InventoryFramework getInstance() {
        return instance == null ? instance = new InventoryFramework() : instance;
    }

    public static Logger getLogger() {
        return getInstance().getPlugin().getLogger();
    }

    public void addDisableHandler(@NotNull Runnable runnable) {
        disableHandlers.add(runnable);
    }

    @EventHandler
    private void handlePluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            disableHandlers.forEach(Runnable::run);
        }
    }
}