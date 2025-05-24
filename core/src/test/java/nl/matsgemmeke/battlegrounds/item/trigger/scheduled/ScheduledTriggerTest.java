package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

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

public class ScheduledTriggerTest {

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
        ScheduledTrigger trigger = new ScheduledTrigger(schedule, false);
        boolean activated = trigger.isActivated();

        assertThat(activated).isFalse();
    }

    @Test
    public void isActivatedReturnsTrueWhenActivated() {
        ScheduledTrigger trigger = new ScheduledTrigger(schedule, false);
        trigger.activate(context);
        boolean activated = trigger.isActivated();

        assertThat(activated).isTrue();
    }

    @Test
    public void activateDoesNotStartScheduleTwiceWhenAlreadyActivated() {
        ScheduledTrigger trigger = new ScheduledTrigger(schedule, false);
        trigger.activate(context);
        trigger.activate(context);

        verify(schedule, times(1)).start();
    }

    @Test
    public void activateStartsScheduleWithTaskThatNotifiesObservablesAndStopsScheduleWhenNotContinuous() {
        ScheduledTrigger trigger = new ScheduledTrigger(schedule, false);
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
        ScheduledTrigger trigger = new ScheduledTrigger(schedule, true);
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
        ScheduledTrigger trigger = new ScheduledTrigger(schedule, false);
        trigger.deactivate();

        verify(schedule).stop();
    }
}
