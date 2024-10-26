package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemMechanism;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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

    private ItemHolder holder;
    private ItemMechanism mechanism;
    private long delayUntilActivation;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        holder = mock(ItemHolder.class);
        mechanism = mock(ItemMechanism.class);
        taskRunner = mock(TaskRunner.class);
        delayUntilActivation = 1L;
    }

    @Test
    public void shouldActivateMechanismForAllDeployedObjectsWhenActivating() {
        Deployable object = mock(Deployable.class);

        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(mock(BukkitTask.class));

        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.primeDeployedObject(holder, object);
        activation.activateDeployedObjects(holder);

        verify(mechanism).activate(holder, object);
    }

    @Test
    public void activateInHolderHandAfterDelay() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.primeInHand(holder, itemStack);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(delayUntilActivation));

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(mechanism).activate(holder, itemStack);
    }

    @Test
    public void activateAtDeployedObjectAfterDelay() {
        Deployable object = mock(Deployable.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.primeInHand(holder, itemStack);
        activation.deploy(object);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(runnableCaptor.capture(), eq(delayUntilActivation));

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(mechanism).activate(holder, object);
    }

    @Test
    public void isPrimedReturnsTrueWhenDeployedObjectsExistAndIsNull() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.primeInHand(holder, itemStack);

        boolean primed = activation.isPrimed();

        assertTrue(primed);
    }

    @Test
    public void isPrimedReturnsFalseWhenActivationHasNoDeployedObjects() {
        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);

        boolean primed = activation.isPrimed();

        assertFalse(primed);
    }

    @Test
    public void isPrimedReturnsFalseWhenMostRecentDeployedObjectIsNotNull() {
        Deployable object = mock(Deployable.class);

        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.primeDeployedObject(holder, object);

        boolean primed = activation.isPrimed();

        assertFalse(primed);
    }

    @Test
    public void shouldAssignDeferredDeployedObject() {
        Deployable object = mock(Deployable.class);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(mock(BukkitTask.class));

        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.primeInHand(holder, itemStack);
        activation.deploy(object);
        activation.activateDeployedObjects(holder);

        verify(mechanism).activate(holder, object);
        verify(mechanism, never()).activate(holder, itemStack);
    }
}
