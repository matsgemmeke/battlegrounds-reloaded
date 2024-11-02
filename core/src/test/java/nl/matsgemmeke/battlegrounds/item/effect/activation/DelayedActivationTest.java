package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DelayedActivationTest {

    private ItemEffect effect;
    private ItemHolder holder;
    private long delayUntilActivation;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        effect = mock(ItemEffect.class);
        holder = mock(ItemHolder.class);
        taskRunner = mock(TaskRunner.class);
        delayUntilActivation = 1L;
    }

    @Test
    public void shouldActivateEffectAtAllSourcesWhenActivating() {
        EffectSource source = mock(EffectSource.class);

        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(mock(BukkitTask.class));

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);
        activation.activateInstantly(holder);

        verify(effect).activate(holder, source);
    }

    @Test
    public void isPrimedReturnsFalseWhenActivationHasNoEffectSources() {
        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);

        boolean primed = activation.isPrimed();

        assertFalse(primed);
    }

    @Test
    public void isPrimedReturnsFalseWhenTheLatestEffectSourceIsDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(true);

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);

        boolean primed = activation.isPrimed();

        assertFalse(primed);
    }

    @Test
    public void isPrimedReturnsTrueWhenTheLatestEffectSourceIsNotDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(false);

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);

        boolean primed = activation.isPrimed();

        assertTrue(primed);
    }

    @Test
    public void primeSourceAndActivateAfterDelay() {
        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.isDeployed()).thenReturn(true);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(task);

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(delayUntilActivation));

        // Execute the runnable afterward to simulate the delay
        runnableCaptor.getValue().run();

        verify(effect).activate(holder, source);
        verify(holder).setHeldItem(null);
        verify(task, never()).cancel();
    }

    @Test
    public void primeSourceAfterPrimingUndeployedSourceAndActivateAfterDelay() {
        EffectSource deployedSource = mock(EffectSource.class);
        when(deployedSource.exists()).thenReturn(true);
        when(deployedSource.isDeployed()).thenReturn(true);

        EffectSource undeployedSource = mock(EffectSource.class);
        when(undeployedSource.exists()).thenReturn(true);
        when(undeployedSource.isDeployed()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(task);

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, undeployedSource);
        activation.prime(holder, deployedSource);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner, times(2)).runTaskLater(runnableCaptor.capture(), eq(delayUntilActivation));

        // Execute the runnables afterward to simulate the delay
        runnableCaptor.getAllValues().forEach(Runnable::run);

        verify(effect, times(1)).activate(any(ItemHolder.class), any(EffectSource.class));
        verify(effect).activate(holder, deployedSource);
        verify(effect, never()).activate(holder, undeployedSource);
    }

    @Test
    public void primeSourceButDoNotActivateIfTheSourceWasActivatedDuringTheDelay() {
        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(task);

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);
        activation.activateInstantly(holder);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(delayUntilActivation));

        // Execute the runnable afterward to simulate the delay
        runnableCaptor.getValue().run();

        verify(effect, atMost(1)).activate(holder, source);
        verify(task).cancel();
    }

    @Test
    public void primeSourceButDoNotActivateIfTheSourceDoesNotExistAnymore() {
        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(false);

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(delayUntilActivation));

        // Execute the runnable afterward to simulate the delay
        runnableCaptor.getValue().run();

        verify(effect, never()).activate(holder, source);
    }
}
