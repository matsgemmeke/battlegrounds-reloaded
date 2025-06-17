package nl.matsgemmeke.battlegrounds.scheduling;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RepeatingScheduleTest {

    private static final long INTERVAL = 1L;
    private static final long DELAY = 5L;

    private BukkitScheduler bukkitScheduler;
    private Plugin plugin;

    @BeforeEach
    public void setUp() {
        bukkitScheduler = mock(BukkitScheduler.class);

        plugin = mock(Plugin.class, Mockito.RETURNS_DEEP_STUBS);
        when(plugin.getServer().getScheduler()).thenReturn(bukkitScheduler);
    }

    @Test
    public void clearTasksRemovesAllTasksFromSchedule() {
        ScheduleTask scheduleTask = mock(ScheduleTask.class);

        RepeatingSchedule schedule = new RepeatingSchedule(plugin);
        schedule.setDelay(DELAY);
        schedule.setInterval(INTERVAL);
        schedule.addTask(scheduleTask);
        schedule.start();
        schedule.clearTasks();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(bukkitScheduler).runTaskTimer(eq(plugin), runnableCaptor.capture(), eq(DELAY), eq(INTERVAL));
        runnableCaptor.getValue().run();

        verify(scheduleTask, never()).run();
    }

    @Test
    public void isRunningReturnsFalseWhenNotRunning() {
        RepeatingSchedule schedule = new RepeatingSchedule(plugin);
        boolean running = schedule.isRunning();

        assertThat(running).isFalse();
    }

    @Test
    public void isRunningReturnsTrueWhenRunning() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        RepeatingSchedule schedule = new RepeatingSchedule(plugin);
        schedule.start();
        boolean running = schedule.isRunning();

        assertThat(running).isTrue();
    }

    @Test
    public void startThrowsScheduleExceptionWhenAlreadyStarted() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        RepeatingSchedule schedule = new RepeatingSchedule(plugin);
        schedule.start();

        assertThatThrownBy(schedule::start).isInstanceOf(ScheduleException.class).hasMessage("Schedule is already running");
    }

    @Test
    public void startRunsTaskTimerWithGivenDelayAndIntervalThatRunsScheduleTasks() {
        long delay = 5L;
        long interval = 1L;
        ScheduleTask scheduleTask = mock(ScheduleTask.class);

        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(delay), eq(interval))).thenReturn(bukkitTask);

        RepeatingSchedule schedule = new RepeatingSchedule(plugin);
        schedule.setDelay(delay);
        schedule.setInterval(interval);
        schedule.addTask(scheduleTask);
        schedule.start();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(bukkitScheduler).runTaskTimer(eq(plugin), runnableCaptor.capture(), eq(delay), eq(interval));

        runnableCaptor.getValue().run();

        verify(scheduleTask).run();
    }

    @Test
    public void stopDoesNotCancelBukkitTaskWhenNotRunning() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        RepeatingSchedule schedule = new RepeatingSchedule(plugin);
        schedule.stop();

        verifyNoInteractions(bukkitTask);
    }

    @Test
    public void stopCancelsBukkitTaskWhenRunning() {
        long delay = 5L;
        long interval = 1L;

        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(delay), eq(interval))).thenReturn(bukkitTask);

        RepeatingSchedule schedule = new RepeatingSchedule(plugin);
        schedule.setDelay(delay);
        schedule.setInterval(interval);
        schedule.start();
        schedule.stop();

        verify(bukkitTask).cancel();
    }
}
