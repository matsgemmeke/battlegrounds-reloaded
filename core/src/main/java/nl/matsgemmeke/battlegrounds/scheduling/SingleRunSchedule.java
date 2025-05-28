package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Schedule that runs only once after a given delay.
 */
public class SingleRunSchedule implements Schedule {

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final Set<ScheduleTask> scheduleTasks;
    @Nullable
    private BukkitTask bukkitTask;
    private long delay;

    @Inject
    public SingleRunSchedule(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.scheduleTasks = new HashSet<>();
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void addTask(@NotNull ScheduleTask task) {
        scheduleTasks.add(task);
    }

    public boolean isRunning() {
        return bukkitTask != null;
    }

    public void start() {
        if (bukkitTask != null) {
            throw new ScheduleException("Schedule is already running");
        }

        bukkitTask = plugin.getServer().getScheduler().runTaskLater(plugin, this::runTasksAndStop, delay);
    }

    private void runTasksAndStop() {
        scheduleTasks.forEach(ScheduleTask::run);
        bukkitTask = null;
    }

    public void stop() {
        if (bukkitTask == null) {
            return;
        }

        bukkitTask.cancel();
        bukkitTask = null;
    }
}
