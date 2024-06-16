package kr.starly.libs.inventory.window;

import kr.starly.libs.inventory.InventoryFramework;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WindowManager implements Listener {

    private static WindowManager instance;

    private final Map<Inventory, AbstractWindow> windowsByInventory = new HashMap<>();
    private final Map<Player, AbstractWindow> windowsByPlayer = new HashMap<>();

    private WindowManager() {
        Bukkit.getPluginManager().registerEvents(this, InventoryFramework.getInstance().getPlugin());
        InventoryFramework.getInstance().addDisableHandler(() -> new HashSet<>(windowsByPlayer.values()).forEach(AbstractWindow::close));
    }

    public static WindowManager getInstance() {
        return instance == null ? instance = new WindowManager() : instance;
    }

    public void addWindow(AbstractWindow window) {
        windowsByInventory.put(window.getInventories()[0], window);
        windowsByPlayer.put(window.getViewer(), window);
    }

    public void removeWindow(AbstractWindow window) {
        windowsByInventory.remove(window.getInventories()[0]);
        windowsByPlayer.remove(window.getViewer());
    }

    public @Nullable Window getWindow(Inventory inventory) {
        return windowsByInventory.get(inventory);
    }

    public @Nullable Window getOpenWindow(Player player) {
        return windowsByPlayer.get(player);
    }

    public Set<Window> getWindows() {
        return new HashSet<>(windowsByInventory.values());
    }

    @EventHandler
    private void handleInventoryClick(InventoryClickEvent event) {
        AbstractWindow window = (AbstractWindow) getOpenWindow((Player) event.getWhoClicked());
        if (window != null) {
            window.handleClickEvent(event);

            if (event.getClick().name().equals("SWAP_OFFHAND") && event.isCancelled()) {
                EntityEquipment equipment = event.getWhoClicked().getEquipment();
                equipment.setItemInOffHand(equipment.getItemInOffHand());
            }
        }
    }

    @EventHandler
    private void handleInventoryDrag(InventoryDragEvent event) {
        AbstractWindow window = (AbstractWindow) getOpenWindow((Player) event.getWhoClicked());
        if (window != null) {
            window.handleDragEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void handleInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        AbstractWindow window = (AbstractWindow) getWindow(event.getInventory());
        if (window != null) {
            window.handleCloseEvent(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void handleInventoryOpen(InventoryOpenEvent event) {
        AbstractWindow window = (AbstractWindow) getWindow(event.getInventory());
        if (window != null) {
            window.handleOpenEvent(event);
        }
    }

    @EventHandler
    private void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AbstractWindow window = (AbstractWindow) getOpenWindow(player);
        if (window != null) {
            window.handleCloseEvent(true);
        }
    }

    @EventHandler
    private void handlePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        AbstractWindow window = (AbstractWindow) getOpenWindow(player);
        if (window != null) {
            window.handleViewerDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void handleItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Window window = getOpenWindow((Player) entity);
            if (window instanceof AbstractDoubleWindow)
                event.setCancelled(true);
        }
    }
}