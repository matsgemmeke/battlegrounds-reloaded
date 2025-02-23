package nl.matsgemmeke.battlegrounds;

import com.google.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to run asynchronous tasks for the Battlegrounds plugin.
 */
public class TaskRunner {

    @NotNull
    private Plugin plugin;

    @Inject
    public TaskRunner(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public BukkitTask runTaskLater(@NotNull BukkitRunnable runnable, long delay) {
        return runnable.runTaskLater(plugin, delay);
    }

    @NotNull
    public BukkitTask runTaskLater(@NotNull Runnable runnable, long delay) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    @NotNull
    public BukkitTask runTaskTimer(@NotNull BukkitRunnable runnable, long delay, long period) {
        return runnable.runTaskTimer(plugin, delay, period);
    }

    @NotNull
    public BukkitTask runTaskTimer(@NotNull Runnable runnable, long delay, long period) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }
}
