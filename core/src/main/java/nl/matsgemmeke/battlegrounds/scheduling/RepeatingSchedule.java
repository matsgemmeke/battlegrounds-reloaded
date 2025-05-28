package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class RepeatingSchedule implements Schedule {

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final Set<ScheduleTask> scheduleTasks;
    @Nullable
    private BukkitTask bukkitTask;
    private long delay;
    private long interval;

    @Inject
    public RepeatingSchedule(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.scheduleTasks = new HashSet<>();
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
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

        bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> scheduleTasks.forEach(ScheduleTask::run), delay, interval);
    }

    public void stop() {
        if (bukkitTask == null) {
            return;
        }

        bukkitTask.cancel();
        bukkitTask = null;
    }
}
