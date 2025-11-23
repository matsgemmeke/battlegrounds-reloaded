package nl.matsgemmeke.battlegrounds.scheduling;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepeatingScheduleTest {

    private static final long DELAY = 1L;
    private static final long INTERVAL = 5L;
    private static final long DURATION = 6L;

    @Mock
    private BukkitScheduler bukkitScheduler;
    @Mock
    private BukkitTask bukkitTask;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Plugin plugin;
    @Mock
    private ScheduleTask scheduleTask;
    @InjectMocks
    private RepeatingSchedule schedule;

    @BeforeEach
    void setUp() {
        when(plugin.getServer().getScheduler()).thenReturn(bukkitScheduler);
    }

    @Test
    void clearTasksRemovesAllTasksFromSchedule() {
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
    void isRunningReturnsFalseWhenNotRunning() {
        boolean running = schedule.isRunning();

        assertThat(running).isFalse();
    }

    @Test
    void isRunningReturnsTrueWhenRunning() {
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        schedule.start();
        boolean running = schedule.isRunning();

        assertThat(running).isTrue();
    }

    @Test
    void startThrowsScheduleExceptionWhenAlreadyStarted() {
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        schedule.start();

        assertThatThrownBy(schedule::start).isInstanceOf(ScheduleException.class).hasMessage("Schedule is already running");
    }

    @Test
    void startRunsTaskTimerWithGivenDelayAndIntervalThatRunsScheduleTasks() {
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(DELAY), eq(INTERVAL))).thenReturn(bukkitTask);

        schedule.setDelay(DELAY);
        schedule.setInterval(INTERVAL);
        schedule.addTask(scheduleTask);
        schedule.start();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(bukkitScheduler).runTaskTimer(eq(plugin), runnableCaptor.capture(), eq(DELAY), eq(INTERVAL));

        runnableCaptor.getValue().run();

        verify(scheduleTask).run();
    }

    @Test
    void startRunsTaskTimerWithGivenDelayAndIntervalThatStopsAfterGivenDuration() {
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(DELAY), eq(INTERVAL))).thenReturn(bukkitTask);

        schedule.addTask(scheduleTask);
        schedule.setDelay(DELAY);
        schedule.setInterval(INTERVAL);
        schedule.setDuration(DURATION);
        schedule.start();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(bukkitScheduler).runTaskTimer(eq(plugin), runnableCaptor.capture(), eq(DELAY), eq(INTERVAL));

        runnableCaptor.getValue().run();
        runnableCaptor.getValue().run();

        verify(scheduleTask, times(1)).run();
        verify(bukkitTask).cancel();
    }

    @Test
    void stopDoesNotCancelBukkitTaskWhenNotRunning() {
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(bukkitTask);

        schedule.start();
        schedule.stop();
        schedule.stop();

        verify(bukkitTask, times(1)).cancel();
    }

    @Test
    void stopCancelsBukkitTaskWhenRunning() {
        when(bukkitScheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(DELAY), eq(INTERVAL))).thenReturn(bukkitTask);

        schedule.setDelay(DELAY);
        schedule.setInterval(INTERVAL);
        schedule.start();
        schedule.stop();

        verify(bukkitTask).cancel();
    }
}
