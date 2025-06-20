package nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst;

import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BurstModeTest {

    private static final int AMOUNT_OF_SHOTS = 2;
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
        BurstMode fireMode = new BurstMode(shotSchedule, cooldownSchedule, AMOUNT_OF_SHOTS, RATE_OF_FIRE);
        boolean cycling = fireMode.isCycling();

        assertThat(cycling).isFalse();
    }

    @Test
    public void isCyclingReturnsTrueWhenHavingStarted() {
        BurstMode fireMode = new BurstMode(shotSchedule, cooldownSchedule, AMOUNT_OF_SHOTS, RATE_OF_FIRE);
        fireMode.startCycle();
        boolean cycling = fireMode.isCycling();

        assertThat(cycling).isTrue();
    }

    @Test
    public void startCycleDoesNotStartSchedulesWhenHavingAlreadyStarted() {
        BurstMode fireMode = new BurstMode(shotSchedule, cooldownSchedule, AMOUNT_OF_SHOTS, RATE_OF_FIRE);
        fireMode.startCycle();
        boolean started = fireMode.startCycle();

        assertThat(started).isFalse();

        verify(shotSchedule, times(1)).start();
        verify(cooldownSchedule, times(1)).start();
    }

    @Test
    public void startCycleStartsShotScheduleThatNotifiesObserversAndCooldownScheduleThatStopsCycle() {
        ShotObserver shotObserver = mock(ShotObserver.class);

        BurstMode fireMode = new BurstMode(shotSchedule, cooldownSchedule, AMOUNT_OF_SHOTS, RATE_OF_FIRE);
        fireMode.addShotObserver(shotObserver);
        boolean started = fireMode.startCycle();

        ArgumentCaptor<ScheduleTask> fireShotScheduleTask = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(shotSchedule).addTask(fireShotScheduleTask.capture());
        fireShotScheduleTask.getValue().run();
        fireShotScheduleTask.getValue().run();

        ArgumentCaptor<ScheduleTask> cancelCycleScheduleTask = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(cooldownSchedule).addTask(cancelCycleScheduleTask.capture());
        cancelCycleScheduleTask.getValue().run();

        assertThat(started).isTrue();

        verify(shotObserver, times(2)).onShotFired();
        verify(shotSchedule, times(2)).stop();
        verify(cooldownSchedule).stop();
    }

    @Test
    public void cancelCycleReturnFalseWhenNotStarted() {
        BurstMode fireMode = new BurstMode(shotSchedule, cooldownSchedule, AMOUNT_OF_SHOTS, RATE_OF_FIRE);
        boolean cancelled = fireMode.cancelCycle();

        assertThat(cancelled).isFalse();
    }

    @Test
    public void cancelCycleReturnTrueAndCancelsCycleWhenHavingStarted() {
        BurstMode fireMode = new BurstMode(shotSchedule, cooldownSchedule, AMOUNT_OF_SHOTS, RATE_OF_FIRE);
        fireMode.startCycle();
        boolean cancelled = fireMode.cancelCycle();

        assertThat(cancelled).isTrue();

        verify(shotSchedule).stop();
        verify(cooldownSchedule).stop();
    }
}
