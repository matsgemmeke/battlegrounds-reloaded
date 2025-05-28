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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SingleRunScheduleTest {

    private static final long DELAY = 10L;

    private BukkitScheduler bukkitScheduler;
    private Plugin plugin;

    @BeforeEach
    public void setUp() {
        bukkitScheduler = mock(BukkitScheduler.class);

        plugin = mock(Plugin.class, Mockito.RETURNS_DEEP_STUBS);
        when(plugin.getServer().getScheduler()).thenReturn(bukkitScheduler);
    }

    @Test
    public void isRunningReturnsFalseWhenNotRunning() {
        SingleRunSchedule schedule = new SingleRunSchedule(plugin);
        boolean running = schedule.isRunning();

        assertThat(running).isFalse();
    }

    @Test
    public void isRunningReturnsTrueWhenRunning() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskLater(eq(plugin), any(Runnable.class), anyLong())).thenReturn(bukkitTask);

        SingleRunSchedule schedule = new SingleRunSchedule(plugin);
        schedule.start();
        boolean running = schedule.isRunning();

        assertThat(running).isTrue();
    }

    @Test
    public void startThrowsScheduleExceptionWhenAlreadyStarted() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskLater(eq(plugin), any(Runnable.class), eq(DELAY))).thenReturn(bukkitTask);

        SingleRunSchedule schedule = new SingleRunSchedule(plugin);
        schedule.setDelay(DELAY);
        schedule.start();

        assertThatThrownBy(schedule::start).isInstanceOf(ScheduleException.class).hasMessage("Schedule is already running");
    }

    @Test
    public void startRunsDelayedBukkitTaskThatRunsAfterGivenDelay() {
        ScheduleTask scheduleTask = mock(ScheduleTask.class);

        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskLater(eq(plugin), any(Runnable.class), eq(DELAY))).thenReturn(bukkitTask);

        SingleRunSchedule schedule = new SingleRunSchedule(plugin);
        schedule.setDelay(DELAY);
        schedule.addTask(scheduleTask);
        schedule.start();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(bukkitScheduler).runTaskLater(eq(plugin), runnableCaptor.capture(), eq(DELAY));

        runnableCaptor.getValue().run();

        verify(scheduleTask).run();
    }

    @Test
    public void stopDoesNotCancelBukkitTaskWhenNotRunning() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        SingleRunSchedule schedule = new SingleRunSchedule(plugin);
        schedule.stop();

        verifyNoInteractions(bukkitTask);
    }

    @Test
    public void stopCancelsBukkitTaskWhenRunning() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskLater(eq(plugin), any(Runnable.class), eq(DELAY))).thenReturn(bukkitTask);

        SingleRunSchedule schedule = new SingleRunSchedule(plugin);
        schedule.setDelay(DELAY);
        schedule.start();
        schedule.stop();

        verify(bukkitTask).cancel();
    }
}
