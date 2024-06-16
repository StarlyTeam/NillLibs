package kr.starly.libs.scheduler;

import lombok.NoArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Do {

    private static Plugin plugin;

    public static void init(Plugin plugin) {
        Do.plugin = plugin;
    }

    public static BukkitTask sync(Run runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(plugin);
    }

    public static BukkitTask syncLater(long delay, Run runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(plugin, delay);
    }

    public static BukkitTask async(Run runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(plugin);
    }

    public static BukkitTask asyncLater(long delay, Run runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLaterAsynchronously(plugin, delay);
    }

    public static BukkitTask syncTimer(long period, Run runnable) {
        return syncTimerLater(0, period, runnable);
    }

    public static BukkitTask syncTimerLater(long delay, long period, Run runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimer(plugin, delay, period);
    }

    public static BukkitTask syncTimer(long period, RunResult<Boolean> runnable) {
        return syncTimerLater(0, period, runnable);
    }

    public static BukkitTask syncTimerLater(long delay, long period, RunResult<Boolean> runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!runnable.run()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, delay, period);
    }

    public static BukkitTask asyncTimer(long period, Run runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimerAsynchronously(plugin, 0, period);
    }

    public static BukkitTask asyncTimer(long period, RunResult<Boolean> runnable) {
        return asyncTimerLater(0, period, runnable);
    }

    public static BukkitTask asyncTimerLater(long delay, long period, RunResult<Boolean> runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!runnable.run()) {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, delay, period);
    }

    public static <T> BukkitTask forAll(Collection<T> objects, boolean async, RunArgument<T> runArgument) {
        return forAll(1, objects, async, runArgument);
    }

    public static <T> BukkitTask forAll(int perTick, Collection<T> objects, boolean async, RunArgument<T> runArgument) {
        return forAll(perTick, objects, async, runArgument, null);
    }

    public static <T> BukkitTask forAll(int perTick, Collection<T> objects, boolean async, RunArgument<T> runArgument, Run onDone) {
        final ArrayList<T> finalObjects = new ArrayList<>(objects);
        return new BukkitRunnable() {
            private int current = 0;

            @Override
            public void run() {
                for (int i = 0; i < perTick; i++) {
                    if (current >= finalObjects.size()) {
                        break;
                    }
                    T object = finalObjects.get(current);
                    try {
                        if (async) {
                            Do.async(() -> runArgument.run(object));
                        } else {
                            Do.sync(() -> runArgument.run(object));
                        }
                    } catch (Exception e) {
                        plugin.getLogger().log(Level.SEVERE, "Do.forAll() iteration failed for object: " + object + (object != null ? " (" + object.getClass().getName() + ")" : ""), e);
                    }
                    current++;
                }
                if (current >= finalObjects.size()) {
                    this.cancel();
                    if (onDone != null) {
                        onDone.run();
                    }
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

}