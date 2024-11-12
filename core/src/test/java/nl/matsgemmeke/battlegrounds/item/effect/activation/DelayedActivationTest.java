package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
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

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).activate(contextCaptor.capture());

        assertEquals(source, contextCaptor.getValue().getSource());
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseWhenActivationHasNoContexts() {
        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);

        boolean awaitingDeployment = activation.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseWhenTheLatestEffectSourceIsDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(true);

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);

        boolean awaitingDeployment = activation.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isPendingDeploymentReturnsTrueWhenTheLatestEffectSourceIsNotDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(false);

        DelayedActivation activation = new DelayedActivation(effect, taskRunner, delayUntilActivation);
        activation.prime(holder, source);

        boolean awaitingDeployment = activation.isAwaitingDeployment();

        assertTrue(awaitingDeployment);
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

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).activate(contextCaptor.capture());

        assertEquals(source, contextCaptor.getValue().getSource());

        verify(holder).setHeldItem(null);
        verify(task, never()).cancel();
    }

    @Test
    public void primeDeployedSourceAfterCreatingPendingDeploymentAndActivateAfterDelay() {
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
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(delayUntilActivation));

        // Execute the runnables afterward to simulate the delay
        runnableCaptor.getAllValues().forEach(Runnable::run);

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).activate(contextCaptor.capture());

        assertEquals(deployedSource, contextCaptor.getValue().getSource());

        verify(holder).setHeldItem(null);
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

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).activate(contextCaptor.capture());

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

        verify(effect, never()).activate(any(ItemEffectContext.class));
    }
}
