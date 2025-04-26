package nl.matsgemmeke.battlegrounds.item.effect.trigger.timed;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.TriggerObserver;
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

    private Deployer deployer;
    private Entity entity;
    private ItemEffectContext context;
    private ItemEffectSource source;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        deployer = mock(Deployer.class);
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        taskRunner = mock(TaskRunner.class);
        context = new ItemEffectContext(deployer, entity, source);
    }

    @Test
    public void cancelDoesNotCancelTaskIfNotPrimed() {
        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.cancel();

        assertThat(trigger.isPrimed()).isFalse();
    }

    @Test
    public void cancelCancelsTaskIfPrimed() {
        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION))).thenReturn(task);

        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.prime(context);
        trigger.cancel();

        assertThat(trigger.isPrimed()).isFalse();

        verify(task).cancel();
    }

    @Test
    public void primeDoesNotScheduleDelayedActivationTwiceIfAlreadyPrimed() {
        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.prime(context);
        trigger.prime(context);

        verify(taskRunner, times(1)).runTaskLater(any(Runnable.class), eq(DELAY_UNTIL_ACTIVATION));
    }

    @Test
    public void primeFinishesActivationAfterDelayAndRemovesItemIfSourceIsDeployed() {
        TriggerObserver observer = mock(TriggerObserver.class);

        when(source.isDeployed()).thenReturn(true);

        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.addObserver(observer);
        trigger.prime(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(DELAY_UNTIL_ACTIVATION));

        runnableCaptor.getValue().run();

        verify(deployer).setHeldItem(null);
        verify(observer).onActivate();
    }

    @Test
    public void primeFinishesActivationAfterDelayAndDoesNotRemoveItemIfSourceIsNotDeployed() {
        TriggerObserver observer = mock(TriggerObserver.class);

        when(source.isDeployed()).thenReturn(false);

        TimedTrigger trigger = new TimedTrigger(taskRunner, DELAY_UNTIL_ACTIVATION);
        trigger.addObserver(observer);
        trigger.prime(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(DELAY_UNTIL_ACTIVATION));

        runnableCaptor.getValue().run();

        verify(deployer, never()).setHeldItem(null);
        verify(observer).onActivate();
    }
}
