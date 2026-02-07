package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SequenceSchedule implements Schedule {

    private static final long RUNNABLE_DELAY = 0L;
    private static final long RUNNABLE_PERIOD = 1L;

    private final Plugin plugin;
    private final Set<ScheduleTask> scheduleTasks;
    @Nullable
    private BukkitTask bukkitTask;
    private List<Long> offsetTicks;
    private long elapsedTicks;

    @Inject
    public SequenceSchedule(Plugin plugin) {
        this.plugin = plugin;
        this.scheduleTasks = new HashSet<>();
        this.offsetTicks = new ArrayList<>();
        this.elapsedTicks = 0;
    }

    public List<Long> getOffsetTicks() {
        return offsetTicks;
    }

    public void setOffsetTicks(List<Long> offsetTicks) {
        this.offsetTicks = offsetTicks;
    }

    @Override
    public void addTask(ScheduleTask task) {
        scheduleTasks.add(task);
    }

    @Override
    public void clearTasks() {
        scheduleTasks.clear();
    }

    @Override
    public boolean isRunning() {
        return bukkitTask != null;
    }

    @Override
    public void start() {
        if (bukkitTask != null) {
            throw new ScheduleException("Schedule is already running");
        }

        bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::checkTick, RUNNABLE_DELAY, RUNNABLE_PERIOD);
    }

    private void checkTick() {
        elapsedTicks++;

        if (offsetTicks.contains(elapsedTicks)) {
            scheduleTasks.forEach(ScheduleTask::run);

            long maxOffsetTick = offsetTicks.stream().max(Long::compareTo).get();

            if (elapsedTicks >= maxOffsetTick) {
                this.stop();
            }
        }
    }

    @Override
    public void stop() {
        if (bukkitTask == null) {
            return;
        }

        bukkitTask.cancel();
        bukkitTask = null;
        elapsedTicks = 0;
    }
}
