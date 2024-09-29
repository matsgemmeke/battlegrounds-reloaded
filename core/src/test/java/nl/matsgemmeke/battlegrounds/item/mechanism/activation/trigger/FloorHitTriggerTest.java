package nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class FloorHitTriggerTest {

    private long periodBetweenChecks;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        periodBetweenChecks = 5L;
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void stopCheckOnceObjectIsRemoved() {
        ItemHolder holder = mock(ItemHolder.class);

        Deployable object = mock(Deployable.class);
        when(object.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(periodBetweenChecks))).thenReturn(task);

        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, periodBetweenChecks);
        trigger.checkTriggerActivation(holder, object);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void notifyObserversOnceBlockBelowObjectIsNotPassable() {
        World world = mock(World.class);
        Location objectLocation = new Location(world, 1, 1, 1);

        Deployable object = mock(Deployable.class);
        when(object.exists()).thenReturn(true);
        when(object.getLocation()).thenReturn(objectLocation);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(true).thenReturn(false);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        BukkitTask task = mock(BukkitTask.class);
        ItemHolder holder = mock(ItemHolder.class);
        TriggerObserver observer = mock(TriggerObserver.class);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(periodBetweenChecks))).thenReturn(task);

        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, periodBetweenChecks);
        trigger.addObserver(observer);
        trigger.checkTriggerActivation(holder, object);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();
        runnable.run();

        verify(observer).onTrigger(holder, object);
        verify(task).cancel();
    }
}
