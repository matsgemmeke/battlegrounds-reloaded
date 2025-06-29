package nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto;

import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SemiAutomaticModeTest {

    private static final int RATE_OF_FIRE = 300;

    private Schedule cooldownSchedule;

    @BeforeEach
    public void setUp() {
        cooldownSchedule = mock(Schedule.class);
    }

    @Test
    public void isCyclingReturnsFalseWhenNotStarted() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(cooldownSchedule, RATE_OF_FIRE);
        boolean cycling = fireMode.isCycling();

        assertThat(cycling).isFalse();
    }

    @Test
    public void isCyclingReturnsTrueWhenHavingStarted() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(cooldownSchedule, RATE_OF_FIRE);
        fireMode.startCycle();
        boolean cycling = fireMode.isCycling();

        assertThat(cycling).isTrue();
    }

    @Test
    public void startCycleReturnsTrueAndCallsObserversWhenHavingStarted() {
        ShotObserver shotObserver = mock(ShotObserver.class);

        SemiAutomaticMode fireMode = new SemiAutomaticMode(cooldownSchedule, RATE_OF_FIRE);
        fireMode.addShotObserver(shotObserver);
        boolean started = fireMode.startCycle();

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(cooldownSchedule).addTask(scheduleTaskCaptor.capture());
        scheduleTaskCaptor.getValue().run();

        assertThat(started).isTrue();

        verify(shotObserver).onShotFired();
        verify(cooldownSchedule).start();
        verify(cooldownSchedule).stop();
    }

    @Test
    public void startCycleDoesNotNotifyObserversWhenAlreadyCycling() {
        ShotObserver shotObserver = mock(ShotObserver.class);

        SemiAutomaticMode fireMode = new SemiAutomaticMode(cooldownSchedule, RATE_OF_FIRE);
        fireMode.startCycle();
        fireMode.addShotObserver(shotObserver);
        boolean started = fireMode.startCycle();

        assertThat(started).isFalse();

        verifyNoInteractions(shotObserver);
        verify(cooldownSchedule, times(1)).start();
    }

    @Test
    public void cancelCycleReturnsFalseWhenNotStarted() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(cooldownSchedule, RATE_OF_FIRE);
        boolean cancelled = fireMode.cancelCycle();

        assertThat(cancelled).isFalse();
    }

    @Test
    public void cancelCycleReturnsTrueWhenStarted() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(cooldownSchedule, RATE_OF_FIRE);
        fireMode.startCycle();
        boolean cancelled = fireMode.cancelCycle();

        assertThat(cancelled).isTrue();

        verify(cooldownSchedule).stop();
    }
}
