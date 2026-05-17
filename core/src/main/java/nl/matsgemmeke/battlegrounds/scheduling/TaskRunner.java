package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class TaskRunner {

    private final BukkitScheduler bukkitScheduler;
    private final Plugin plugin;

    @Inject
    public TaskRunner(BukkitScheduler bukkitScheduler, Plugin plugin) {
        this.bukkitScheduler = bukkitScheduler;
        this.plugin = plugin;
    }

    public void runTaskAsynchronously(Runnable runnable) {
        bukkitScheduler.runTaskAsynchronously(plugin, runnable);
    }
}
