package nl.matsgemmeke.battlegrounds.scheduling;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class SequenceScheduleTest {

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
        SequenceSchedule schedule = new SequenceSchedule(plugin);
        boolean running = schedule.isRunning();

        assertThat(running).isFalse();
    }

    @Test
    public void isRunningReturnsTrueWhenRunning() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        SequenceSchedule schedule = new SequenceSchedule(plugin);
        schedule.start();
        boolean running = schedule.isRunning();

        assertThat(running).isTrue();
    }

    @Test
    public void startThrowsScheduleExceptionWhenAlreadyStarted() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        SequenceSchedule schedule = new SequenceSchedule(plugin);
        schedule.start();

        assertThatThrownBy(schedule::start).isInstanceOf(ScheduleException.class).hasMessage("Schedule is already running");
    }

    @Test
    public void startRunsTaskTimerThatRunsScheduleTasksEveryTimeAfterGivenDelaysHaveElapsed() {
        List<Long> offsetTicks = List.of(2L);
        ScheduleTask scheduleTask = mock(ScheduleTask.class);

        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(1L))).thenReturn(bukkitTask);

        SequenceSchedule schedule = new SequenceSchedule(plugin);
        schedule.setOffsetTicks(offsetTicks);
        schedule.addTask(scheduleTask);
        schedule.start();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(bukkitScheduler).runTaskTimer(eq(plugin), runnableCaptor.capture(), eq(0L), eq(1L));

        runnableCaptor.getValue().run();
        runnableCaptor.getValue().run();

        verify(scheduleTask, times(1)).run();
        verify(bukkitTask).cancel();
    }

    @Test
    public void stopDoesNotCancelBukkitTaskWhenNotRunning() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        SequenceSchedule schedule = new SequenceSchedule(plugin);
        schedule.stop();

        verifyNoInteractions(bukkitTask);
    }

    @Test
    public void stopCancelsBukkitTaskWhenRunning() {
        BukkitTask bukkitTask = mock(BukkitTask.class);
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(1L))).thenReturn(bukkitTask);

        SequenceSchedule schedule = new SequenceSchedule(plugin);
        schedule.start();
        schedule.stop();

        verify(bukkitTask).cancel();
    }
}
