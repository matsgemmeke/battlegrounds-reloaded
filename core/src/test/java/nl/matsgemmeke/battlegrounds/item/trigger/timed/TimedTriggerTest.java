package nl.matsgemmeke.battlegrounds.item.trigger.timed;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TimedTriggerTest {

    private static final long DELAY_UNTIL_ACTIVATION = 1L;

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
    public void activateDoesNotStartScheduleTwiceWhenAlreadyActivated() {
        TimedTrigger trigger = new TimedTrigger(schedule);
        trigger.activate(context);
        trigger.activate(context);

        verify(schedule, times(1)).start();
    }

    @Test
    public void activateStartsScheduleWithTaskThatNotifiesObservablesAndStopsSchedule() {
        TimedTrigger trigger = new TimedTrigger(schedule);
        trigger.addObserver(observer);
        trigger.activate(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer).onActivate();
        verify(schedule).stop();
    }

    @Test
    public void deactivateStopsSchedule() {
        TimedTrigger trigger = new TimedTrigger(schedule);
        trigger.deactivate();

        verify(schedule).stop();
    }
}
