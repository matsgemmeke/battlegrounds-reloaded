package nl.matsgemmeke.battlegrounds.scheduling;

import com.google.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SequenceSchedule implements Schedule {

    private static final long RUNNABLE_DELAY = 0L;
    private static final long RUNNABLE_PERIOD = 1L;

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final Set<ScheduleTask> scheduleTasks;
    @Nullable
    private BukkitTask bukkitTask;
    @NotNull
    private List<Long> offsetTicks;
    private long elapsedTicks;

    @Inject
    public SequenceSchedule(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.scheduleTasks = new HashSet<>();
        this.offsetTicks = new ArrayList<>();
        this.elapsedTicks = 0;
    }

    @NotNull
    public List<Long> getOffsetTicks() {
        return offsetTicks;
    }

    public void setOffsetTicks(@NotNull List<Long> offsetTicks) {
        this.offsetTicks = offsetTicks;
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

    public void stop() {
        if (bukkitTask == null) {
            return;
        }

        bukkitTask.cancel();
        bukkitTask = null;
        elapsedTicks = 0;
    }
}
