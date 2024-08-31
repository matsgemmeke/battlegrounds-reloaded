package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

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
        activation.prime(holder, null);
        activation.prime(holder, object);
        activation.activate(holder);

        verify(mechanism).activate(holder);
        verify(mechanism).activate(holder, object);
    }

    @Test
    public void isPrimingReturnsTrueWhenDeployedObjectsExistAndIsNull() {
        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.prime(holder, null);

        boolean priming = activation.isPriming();

        assertTrue(priming);
    }

    @Test
    public void isPrimingReturnsFalseWhenActivationHasNoDeployedObjects() {
        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);

        boolean priming = activation.isPriming();

        assertFalse(priming);
    }

    @Test
    public void isPrimingReturnsFalseWhenMostRecentDeployedObjectIsNotNull() {
        Deployable object = mock(Deployable.class);

        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.prime(holder, object);

        boolean priming = activation.isPriming();

        assertFalse(priming);
    }

    @Test
    public void shouldAssignDeferredDeployedObject() {
        Deployable object = mock(Deployable.class);

        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(mock(BukkitTask.class));

        DelayedActivation activation = new DelayedActivation(mechanism, taskRunner, delayUntilActivation);
        activation.prime(holder, null);
        activation.onDeployDeferredObject(object);
        activation.activate(holder);

        verify(mechanism).activate(holder, object);
        verify(mechanism, never()).activate(holder);
    }
}
