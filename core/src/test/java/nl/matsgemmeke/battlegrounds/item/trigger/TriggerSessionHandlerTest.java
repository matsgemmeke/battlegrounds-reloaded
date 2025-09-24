package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Supplier;

import static org.mockito.Mockito.*;

public class TriggerSessionHandlerTest {

    private Schedule schedule;
    private Supplier<Schedule> scheduleSupplier;
    private TriggerNew trigger;

    @BeforeEach
    public void setUp() {
        schedule = mock(Schedule.class);
        scheduleSupplier = () -> schedule;
        trigger = mock(TriggerNew.class);
    }

    @Test
    public void createSessionReturnsTriggerSessionWithStartedScheduleThatDoesNotifyObservers() {
        Entity entity = mock(Entity.class);
        TriggerTarget target = mock(TriggerTarget.class);
        TriggerContext context = new TriggerContext(entity, target);
        TriggerObserver observer = mock(TriggerObserver.class);

        when(trigger.activates(context)).thenReturn(false);

        TriggerSessionHandler sessionHandler = new TriggerSessionHandler(trigger, scheduleSupplier);
        TriggerSession session = sessionHandler.createSession(context);
        session.addObserver(observer);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer, never()).onActivate();
    }

    @Test
    public void createSessionReturnsTriggerSessionWithStartedScheduleThatNotifiesObservers() {
        Entity entity = mock(Entity.class);
        TriggerTarget target = mock(TriggerTarget.class);
        TriggerContext context = new TriggerContext(entity, target);
        TriggerObserver observer = mock(TriggerObserver.class);

        when(trigger.activates(context)).thenReturn(true);

        TriggerSessionHandler sessionHandler = new TriggerSessionHandler(trigger, scheduleSupplier);
        TriggerSession session = sessionHandler.createSession(context);
        session.addObserver(observer);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();

        verify(observer).onActivate();
    }
}
