package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class RepeatingSchedule implements Schedule {

    private final Plugin plugin;
    private final Set<ScheduleTask> scheduleTasks;
    private BukkitTask bukkitTask;
    private long delay;
    private long duration;
    private long elapsedTicks;
    private long interval;

    @Inject
    public RepeatingSchedule(Plugin plugin) {
        this.plugin = plugin;
        this.scheduleTasks = new HashSet<>();
        this.duration = Long.MAX_VALUE;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void addTask(ScheduleTask task) {
        scheduleTasks.add(task);
    }

    public void clearTasks() {
        scheduleTasks.clear();
    }

    public boolean isRunning() {
        return bukkitTask != null;
    }

    public void start() {
        if (bukkitTask != null) {
            throw new ScheduleException("Schedule is already running");
        }

        elapsedTicks = 0;
        bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::performTasks, delay, interval);
    }

    private void performTasks() {
        elapsedTicks += interval;

        if (elapsedTicks > duration) {
            bukkitTask.cancel();
            return;
        }

        scheduleTasks.forEach(ScheduleTask::run);
    }

    public void stop() {
        if (bukkitTask == null) {
            return;
        }

        bukkitTask.cancel();
        bukkitTask = null;
    }
}
