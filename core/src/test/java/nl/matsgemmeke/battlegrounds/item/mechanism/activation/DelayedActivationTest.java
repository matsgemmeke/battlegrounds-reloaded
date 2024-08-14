package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DelayedActivationTest {

    private Droppable item;
    private ItemHolder holder;
    private ItemMechanism mechanism;
    private long delayUntilActivation;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        item = mock(Droppable.class);
        holder = mock(ItemHolder.class);
        mechanism = mock(ItemMechanism.class);
        taskRunner = mock(TaskRunner.class);
        delayUntilActivation = 1L;
    }

    @Test
    public void shouldCancelCurrentTaskAndActivateMechanismWhenActivatingInstantly() {
        Item droppedItem = mock(Item.class);
        when(item.getDroppedItem()).thenReturn(droppedItem);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).thenReturn(task);

        DelayedActivation activation = new DelayedActivation(item, mechanism, taskRunner, delayUntilActivation);
        activation.prime(holder);
        activation.activate(holder);

        verify(task).cancel();
        verify(mechanism).activate(droppedItem, holder);
    }

    @Test
    public void shouldScheduleDelayedTaskWhenPriming() {
        DelayedActivation activation = new DelayedActivation(item, mechanism, taskRunner, delayUntilActivation);
        activation.prime(holder);
        boolean primed = activation.isPrimed();

        assertTrue(primed);

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayUntilActivation));
    }

    @Test
    public void shouldActivateMechanismAtItemLocationIfDropped() {
        Location itemLocation = new Location(null, 1, 1, 1);

        Item droppedItem = mock(Item.class);
        when(droppedItem.getLocation()).thenReturn(itemLocation);

        when(item.getDroppedItem()).thenReturn(droppedItem);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilActivation))).then(answer -> {
            answer.getArgument(0, Runnable.class).run();
            return null;
        });

        DelayedActivation activation = new DelayedActivation(item, mechanism, taskRunner, delayUntilActivation);
        activation.prime(holder);

        verify(mechanism).activate(droppedItem, holder);
    }
}
