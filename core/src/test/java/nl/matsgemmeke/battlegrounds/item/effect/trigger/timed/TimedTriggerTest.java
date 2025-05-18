package nl.matsgemmeke.battlegrounds.item.effect.trigger.timed;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.item.trigger.timed.TimedTrigger;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TimedTriggerTest {

    private static final long DELAY_UNTIL_ACTIVATION = 1L;

    private TaskRunner taskRunner;
    private TriggerContext context;

    @BeforeEach
    public void setUp() {
        taskRunner = mock(TaskRunner.class);
        context = new TriggerContext(mock(Entity.class), mock(TriggerTarget.class));
    }

    @Test
    public void deactivateDoesNotCancelTaskWhenNotActivated() {
        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.deactivate();

        assertThat(trigger.isActivated()).isFalse();
    }

    @Test
    public void deactivateCancelsTaskWhenActivated() {
        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION))).thenReturn(task);

        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.activate(context);
        trigger.deactivate();

        assertThat(trigger.isActivated()).isFalse();

        verify(task).cancel();
    }

    @Test
    public void activateDoesNotScheduleDelayedActivationTwiceWhenAlreadyActivated() {
        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.activate(context);
        trigger.activate(context);

        verify(taskRunner, times(1)).runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION));
    }

    @Test
    public void activateStartsRunnableThatNotifiesObservablesAfterDelay() {
        TriggerObserver observer = mock(TriggerObserver.class);

        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.addObserver(observer);
        trigger.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(DELAY_UNTIL_ACTIVATION));

        runnableCaptor.getValue().run();

        verify(observer).onActivate();
    }
}
