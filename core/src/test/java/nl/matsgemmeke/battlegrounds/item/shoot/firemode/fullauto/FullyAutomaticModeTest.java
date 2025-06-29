package nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto;

import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FullyAutomaticModeTest {

    private static final int RATE_OF_FIRE = 600;

    private Schedule shotSchedule;
    private Schedule cooldownSchedule;

    @BeforeEach
    public void setUp() {
        shotSchedule = mock(Schedule.class);
        cooldownSchedule = mock(Schedule.class);
    }

    @Test
    public void isCyclingReturnsFalseWhenNotStarted() {
        FullyAutomaticMode fireMode = new FullyAutomaticMode(shotSchedule, cooldownSchedule, RATE_OF_FIRE);
        boolean cycling = fireMode.isCycling();

        assertThat(cycling).isFalse();
    }

    @Test
    public void isCyclingReturnsTrueWhenHavingStarted() {
        FullyAutomaticMode fireMode = new FullyAutomaticMode(shotSchedule, cooldownSchedule, RATE_OF_FIRE);
        fireMode.startCycle();
        boolean cycling = fireMode.isCycling();

        assertThat(cycling).isTrue();
    }

    @Test
    public void startCycleDoesNotStartSchedulesWhenHavingAlreadyStarted() {
        FullyAutomaticMode fireMode = new FullyAutomaticMode(shotSchedule, cooldownSchedule, RATE_OF_FIRE);
        fireMode.startCycle();
        boolean started = fireMode.startCycle();

        assertThat(started).isFalse();

        verify(shotSchedule, times(1)).start();
        verify(cooldownSchedule, times(1)).start();
    }

    @Test
    public void startCycleStartsShotScheduleThatNotifiesObserversAndCooldownScheduleThatStopsCycle() {
        ShotObserver shotObserver = mock(ShotObserver.class);

        FullyAutomaticMode fireMode = new FullyAutomaticMode(shotSchedule, cooldownSchedule, RATE_OF_FIRE);
        fireMode.addShotObserver(shotObserver);
        boolean started = fireMode.startCycle();

        ArgumentCaptor<ScheduleTask> notifyObserversScheduleTask = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(shotSchedule).addTask(notifyObserversScheduleTask.capture());
        notifyObserversScheduleTask.getValue().run();

        ArgumentCaptor<ScheduleTask> cancelCycleScheduleTask = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(cooldownSchedule).addTask(cancelCycleScheduleTask.capture());
        cancelCycleScheduleTask.getValue().run();

        assertThat(started).isTrue();

        verify(shotObserver).onShotFired();
        verify(shotSchedule).stop();
        verify(cooldownSchedule).stop();
    }

    @Test
    public void cancelCycleReturnFalseWhenNotStarted() {
        FullyAutomaticMode fireMode = new FullyAutomaticMode(shotSchedule, cooldownSchedule, RATE_OF_FIRE);
        boolean cancelled = fireMode.cancelCycle();

        assertThat(cancelled).isFalse();
    }

    @Test
    public void cancelCycleReturnTrueAndCancelsCycleWhenHavingStarted() {
        FullyAutomaticMode fireMode = new FullyAutomaticMode(shotSchedule, cooldownSchedule, RATE_OF_FIRE);
        fireMode.startCycle();
        boolean cancelled = fireMode.cancelCycle();

        assertThat(cancelled).isTrue();

        verify(shotSchedule).stop();
        verify(cooldownSchedule).stop();
    }
}
