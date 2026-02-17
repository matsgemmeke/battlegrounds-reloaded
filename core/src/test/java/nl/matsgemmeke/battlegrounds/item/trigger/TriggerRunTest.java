package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import org.junit.jupiter.api.DisplayName;
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
    private Actor actor;
    @Mock
    private Schedule schedule;
    @Mock
    private Trigger trigger;
    @Spy
    private TriggerContext context = new TriggerContext(UUID.randomUUID(), actor);
    @InjectMocks
    private TriggerRun triggerRun;

    @Test
    @DisplayName("notifyObservers does not notify observers when not started")
    void notifyObservers_whenNotStarted() {
        TriggerObserver triggerObserver = mock(TriggerObserver.class);

        triggerRun.addObserver(triggerObserver);
        triggerRun.notifyObservers();

        verifyNoInteractions(triggerObserver);
    }

    @Test
    @DisplayName("notifyObservers notifies observers with a SimpleTriggerResult")
    void notifyObservers_whenStarted() {
        TriggerObserver triggerObserver = mock(TriggerObserver.class);

        triggerRun.addObserver(triggerObserver);
        triggerRun.start();
        triggerRun.notifyObservers();

        verify(triggerObserver).onActivate(SimpleTriggerResult.ACTIVATES);
    }

    @Test
    @DisplayName("replaceActor sets new context with replaced actor")
    void replaceActor_setsNewContext() {
        Actor newActor = mock(Actor.class);
        TriggerResult triggerResult = mock(TriggerResult.class);

        when(trigger.check(any(TriggerContext.class))).thenReturn(triggerResult);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        triggerRun.replaceActor(newActor);
        triggerRun.start();

        verify(trigger).check(argThat(context -> context.actor() == newActor));
    }

    @Test
    @DisplayName("cancel does not stop schedule when not started")
    void cancel_whenNotStarted() {
        triggerRun.cancel();

        verify(schedule, never()).stop();
    }

    @Test
    @DisplayName("cancel stops schedule when started")
    void cancel_whenStarted() {
        triggerRun.start();
        triggerRun.cancel();

        verify(schedule).stop();
    }

    @Test
    @DisplayName("start does not start schedule when already started")
    void start_whenAlreadyStarted() {
        triggerRun.start();
        triggerRun.start();

        verify(schedule, times(1)).addTask(any(ScheduleTask.class));
        verify(schedule, times(1)).start();
    }

    @Test
    @DisplayName("start starts schedule with task that does not notify observers when TriggerResult does not activate")
    void start_whenTriggerResultDoesNotActivate() {
        TriggerObserver observer = mock(TriggerObserver.class);

        TriggerResult triggerResult = mock(TriggerResult.class);
        when(triggerResult.activates()).thenReturn(false);

        when(trigger.check(context)).thenReturn(triggerResult);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(schedule).addTask(any(ScheduleTask.class));

        triggerRun.addObserver(observer);
        triggerRun.start();

        verify(schedule).start();
        verifyNoInteractions(observer);
    }

    @Test
    @DisplayName("start starts schedule with task that stops itself when TriggerResult activates")
    void start_withNonRepeatingSchedule() {
        TriggerObserver observer = mock(TriggerObserver.class);

        TriggerResult triggerResult = mock(TriggerResult.class);
        when(triggerResult.activates()).thenReturn(true);

        when(trigger.check(context)).thenReturn(triggerResult);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(schedule).addTask(any(ScheduleTask.class));

        triggerRun.addObserver(observer);
        triggerRun.start();

        verify(schedule).start();
        verify(schedule).stop();
        verify(observer).onActivate(triggerResult);
    }

    @Test
    @DisplayName("start starts schedule with task that continues when TriggerResult activates")
    void start_withRepeatingSchedule() {
        TriggerObserver observer = mock(TriggerObserver.class);

        TriggerResult triggerResult = mock(TriggerResult.class);
        when(triggerResult.activates()).thenReturn(true);

        when(trigger.check(context)).thenReturn(triggerResult);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(schedule).addTask(any(ScheduleTask.class));

        triggerRun.addObserver(observer);
        triggerRun.setRepeating(true);
        triggerRun.start();

        verify(schedule).start();
        verify(schedule, never()).stop();
        verify(observer).onActivate(triggerResult);
    }
}
