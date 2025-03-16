package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.floor.FloorHitTrigger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class FloorHitTriggerTest {

    private static final long PERIOD_BETWEEN_CHECKS = 5L;

    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void cancelDoesNotCancelTriggerCheckIfNotActivated() {
        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);

        assertDoesNotThrow(trigger::cancel);
    }

    @Test
    public void cancelCancelsTriggerCheck() {
        Deployer deployer = mock(Deployer.class);
        LivingEntity entity = mock(LivingEntity.class);
        ItemEffectSource source = mock(ItemEffectSource.class);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);
        trigger.checkTriggerActivation(context);
        trigger.cancel();

        verify(task).cancel();
    }

    @Test
    public void stopsCheckingOnceSourceNoLongerExists() {
        Deployer deployer = mock(Deployer.class);
        LivingEntity entity = mock(LivingEntity.class);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);
        trigger.checkTriggerActivation(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void notifyObserversOnceBlockBelowObjectIsNotPassable() {
        Deployer deployer = mock(Deployer.class);
        LivingEntity entity = mock(LivingEntity.class);
        TriggerObserver observer = mock(TriggerObserver.class);
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(true).thenReturn(false);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);
        trigger.addObserver(observer);
        trigger.checkTriggerActivation(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();
        runnable.run();

        verify(observer).onTrigger();
        verify(task).cancel();
    }
}
