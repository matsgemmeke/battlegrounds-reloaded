package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.enemy.EnemyProximityTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class EnemyProximityTriggerTest {

    private static final double CHECKING_RANGE = 2.5;
    private static final long PERIOD_BETWEEN_CHECKS = 5L;

    private TargetFinder targetFinder;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        targetFinder = mock(TargetFinder.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void cancelDoesNotCancelTriggerCheckIfNotActivated() {
        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);

        assertDoesNotThrow(trigger::cancel);
    }

    @Test
    public void cancelCancelsTriggerCheck() {
        Deployer deployer = mock(Deployer.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.checkTriggerActivation(context);
        trigger.cancel();

        verify(task).cancel();
    }

    @Test
    public void stopsCheckingOnceSourceNoLongerExists() {
        BukkitTask task = mock(BukkitTask.class);
        Deployer deployer = mock(Deployer.class);
        Entity entity = mock(Entity.class);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.exists()).thenReturn(false);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.checkTriggerActivation(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void doNothingInCurrentCheckWhenNoEnemyTargetsAreInsideCheckingRange() {
        BukkitTask task = mock(BukkitTask.class);
        Deployer deployer = mock(Deployer.class);
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        Location sourceLocation = new Location(null, 1, 1, 1);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        when(targetFinder.findEnemyTargets(entityId, sourceLocation, CHECKING_RANGE)).thenReturn(Collections.emptyList());
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.checkTriggerActivation(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task, never()).cancel();
    }

    @Test
    public void notifyObserversWhenEnemyTargetsAreInsideCheckingRange() {
        BukkitTask task = mock(BukkitTask.class);
        Deployer deployer = mock(Deployer.class);
        TriggerObserver observer = mock(TriggerObserver.class);
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        Location sourceLocation = new Location(null, 1, 1, 1);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        when(targetFinder.findEnemyTargets(entityId, sourceLocation, CHECKING_RANGE)).thenReturn(List.of(mock(GameEntity.class)));
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.addObserver(observer);
        trigger.checkTriggerActivation(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(observer).onTrigger();
    }
}
