package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriggerRunTest {

    @Mock
    private Schedule schedule;
    @Mock
    private Trigger trigger;
    @Mock
    private TriggerTarget target;
    @Spy
    private TriggerContext context = new TriggerContext(mock(Entity.class), target);
    @InjectMocks
    private TriggerRun triggerRun;

    @Test
    void cancelDoesNotStopScheduleWhenNotStarted() {
        triggerRun.cancel();

        verify(schedule, never()).stop();
    }

    @Test
    void cancelStopsScheduleWhenStarted() {
        triggerRun.start();
        triggerRun.cancel();

        verify(schedule).stop();
    }

    @Test
    void startDoesNotStartScheduleWhenAlreadyStarted() {
        triggerRun.start();
        triggerRun.start();

        verify(schedule, times(1)).addTask(any(ScheduleTask.class));
        verify(schedule, times(1)).start();
    }

    @Test
    void startStartsScheduleWithTaskThatDoesNotNotifyObserversWhenTriggerDoesNotActivate() {
        TriggerObserver observer = mock(TriggerObserver.class);

        when(trigger.activates(context)).thenReturn(false);

        triggerRun.addObserver(observer);
        triggerRun.start();

        ArgumentCaptor<ScheduleTask> taskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(taskCaptor.capture());

        taskCaptor.getValue().run();

        verify(schedule).start();
        verifyNoInteractions(observer);
    }

    @Test
    void startStartsScheduleWithTaskThatNotifiesObserversWhenTriggerActivates() {
        TriggerObserver observer = mock(TriggerObserver.class);

        when(trigger.activates(context)).thenReturn(true);

        triggerRun.addObserver(observer);
        triggerRun.start();

        ArgumentCaptor<ScheduleTask> taskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(taskCaptor.capture());

        taskCaptor.getValue().run();

        verify(schedule).start();
        verify(observer).onActivate();
    }
}
