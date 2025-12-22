package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
    private TriggerContext context = new TriggerContext(UUID.randomUUID(), target);
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
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(schedule).addTask(any(ScheduleTask.class));

        triggerRun.addObserver(observer);
        triggerRun.start();

        verify(schedule).start();
        verifyNoInteractions(observer);
    }

    @Test
    void startStartsScheduleWithTaskThatNotifiesObserversWhenTriggerActivatesAndStopsItselfWhenRepeatingIsFalse() {
        TriggerObserver observer = mock(TriggerObserver.class);

        when(trigger.activates(context)).thenReturn(true);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(schedule).addTask(any(ScheduleTask.class));

        triggerRun.addObserver(observer);
        triggerRun.start();

        verify(schedule).start();
        verify(schedule).stop();
        verify(observer).onActivate();
    }

    @Test
    void startStartsScheduleWithTaskThatNotifiesObserversWhenTriggerActivatesAndContinuesWhenRepeatingIsTrue() {
        TriggerObserver observer = mock(TriggerObserver.class);

        when(trigger.activates(context)).thenReturn(true);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(schedule).addTask(any(ScheduleTask.class));

        triggerRun.addObserver(observer);
        triggerRun.setRepeating(true);
        triggerRun.start();

        verify(schedule).start();
        verify(schedule, never()).stop();
        verify(observer).onActivate();
    }
}
