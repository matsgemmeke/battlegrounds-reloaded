package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TriggerRunTest {

    private Entity entity;
    private Schedule schedule;
    private TriggerContext context;
    private TriggerNew trigger;
    private TriggerTarget target;

    @BeforeEach
    public void setUp() {
        entity = mock(Entity.class);
        schedule = mock(Schedule.class);
        trigger = mock(TriggerNew.class);
        target = mock(TriggerTarget.class);
        context = new TriggerContext(entity, target);
    }

    @Test
    public void cancelDoesNotStopScheduleWhenNotStarted() {
        TriggerRun triggerRun = new TriggerRun(schedule, trigger, context);
        triggerRun.cancel();

        verify(schedule, never()).stop();
    }

    @Test
    public void cancelStopsScheduleWhenStarted() {
        TriggerRun triggerRun = new TriggerRun(schedule, trigger, context);
        triggerRun.start();
        triggerRun.cancel();

        verify(schedule).stop();
    }

    @Test
    public void startDoesNotStartScheduleWhenAlreadyStarted() {
        TriggerRun triggerRun = new TriggerRun(schedule, trigger, context);
        triggerRun.start();
        triggerRun.start();

        verify(schedule, times(1)).addTask(any(ScheduleTask.class));
        verify(schedule, times(1)).start();
    }

    @Test
    public void startStartsScheduleWithTaskThatDoesNotNotifyObserversWhenTriggerDoesNotActivate() {
        TriggerObserver observer = mock(TriggerObserver.class);

        when(target.exists()).thenReturn(true);
        when(trigger.activates(context)).thenReturn(false);

        TriggerRun triggerRun = new TriggerRun(schedule, trigger, context);
        triggerRun.addObserver(observer);
        triggerRun.start();

        ArgumentCaptor<ScheduleTask> taskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(taskCaptor.capture());

        taskCaptor.getValue().run();

        verify(schedule).start();
        verifyNoInteractions(observer);
    }

    @Test
    public void startStartsScheduleWithTaskThatNotifiesObserversWhenTriggerActivates() {
        TriggerObserver observer = mock(TriggerObserver.class);

        when(target.exists()).thenReturn(true);
        when(trigger.activates(context)).thenReturn(true);

        TriggerRun triggerRun = new TriggerRun(schedule, trigger, context);
        triggerRun.addObserver(observer);
        triggerRun.start();

        ArgumentCaptor<ScheduleTask> taskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(taskCaptor.capture());

        taskCaptor.getValue().run();

        verify(schedule).start();
        verify(observer).onActivate();
    }
}
