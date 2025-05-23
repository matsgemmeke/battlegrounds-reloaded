package nl.matsgemmeke.battlegrounds.item.trigger.delayed;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DelayedTriggerTest {

    private Schedule schedule;
    private TriggerContext context;
    private TriggerObserver observer;

    @BeforeEach
    public void setUp() {
        schedule = mock(Schedule.class);
        context = new TriggerContext(mock(Entity.class), mock(TriggerTarget.class));
        observer = mock(TriggerObserver.class);
    }

    @Test
    public void isActivatedReturnsFalseWhenNotActivated() {
        DelayedTrigger trigger = new DelayedTrigger(schedule, false);
        boolean activated = trigger.isActivated();

        assertThat(activated).isFalse();
    }

    @Test
    public void isActivatedReturnsTrueWhenActivated() {
        DelayedTrigger trigger = new DelayedTrigger(schedule, false);
        trigger.activate(context);
        boolean activated = trigger.isActivated();

        assertThat(activated).isTrue();
    }

    @Test
    public void activateDoesNotStartScheduleTwiceWhenAlreadyActivated() {
        DelayedTrigger trigger = new DelayedTrigger(schedule, false);
        trigger.activate(context);
        trigger.activate(context);

        verify(schedule, times(1)).start();
    }

    @Test
    public void activateStartsScheduleWithTaskThatNotifiesObservablesAndStopsScheduleWhenNotContinuous() {
        DelayedTrigger trigger = new DelayedTrigger(schedule, false);
        trigger.addObserver(observer);
        trigger.activate(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer).onActivate();
        verify(schedule).start();
        verify(schedule).stop();
    }

    @Test
    public void activateStartsScheduleWithTaskThatNotifiesObservablesAndDoesNotStopScheduleWhenContinuous() {
        DelayedTrigger trigger = new DelayedTrigger(schedule, true);
        trigger.addObserver(observer);
        trigger.activate(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer).onActivate();
        verify(schedule).start();
        verify(schedule, never()).stop();
    }

    @Test
    public void deactivateStopsSchedule() {
        DelayedTrigger trigger = new DelayedTrigger(schedule, false);
        trigger.deactivate();

        verify(schedule).stop();
    }
}
