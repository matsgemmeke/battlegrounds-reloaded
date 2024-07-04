package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DelayedActivationTest {

    private Droppable item;
    private ItemMechanism mechanism;
    private long delayUntilTrigger;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        item = mock(Droppable.class);
        mechanism = mock(ItemMechanism.class);
        taskRunner = mock(TaskRunner.class);
        delayUntilTrigger = 1L;
    }

    @Test
    public void shouldScheduleDelayedTaskWhenPriming() {
        ItemHolder holder = mock(ItemHolder.class);

        DelayedActivation activation = new DelayedActivation(item, mechanism, taskRunner, delayUntilTrigger);
        activation.prime(holder);
        boolean primed = activation.isPrimed();

        assertTrue(primed);

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(delayUntilTrigger));
    }

    @Test
    public void shouldActivateMechanismAtItemLocationIfDropped() {
        ItemHolder holder = mock(ItemHolder.class);
        Location itemLocation = new Location(null, 1, 1, 1);

        Item droppedItem = mock(Item.class);
        when(droppedItem.getLocation()).thenReturn(itemLocation);

        when(item.getDroppedItem()).thenReturn(droppedItem);
        when(taskRunner.runTaskLater(any(Runnable.class), eq(delayUntilTrigger))).then(answer -> {
            answer.getArgument(0, Runnable.class).run();
            return null;
        });

        DelayedActivation activation = new DelayedActivation(item, mechanism, taskRunner, delayUntilTrigger);
        activation.prime(holder);

        verify(mechanism).activate(droppedItem, holder);
    }
}
