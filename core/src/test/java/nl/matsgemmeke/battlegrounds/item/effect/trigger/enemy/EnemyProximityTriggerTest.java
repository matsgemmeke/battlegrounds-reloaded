package nl.matsgemmeke.battlegrounds.item.effect.trigger.enemy;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
    public void isPrimedReturnsFalseWhenTriggerIsNotPrimed() {
        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        boolean primed = trigger.isPrimed();

        assertThat(primed).isFalse();
    }

    @Test
    public void isPrimedReturnsTrueWhenTriggerIsPrimed() {
        Deployer deployer = mock(Deployer.class);
        Entity entity = mock(Entity.class);
        ItemEffectSource source = mock(ItemEffectSource.class);
        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.prime(context);
        boolean primed = trigger.isPrimed();

        assertThat(primed).isTrue();
    }

    @Test
    public void cancelDoesNotCancelTriggerCheckIfNotActivated() {
        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);

        assertThatCode(trigger::cancel).doesNotThrowAnyException();
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
        trigger.prime(context);
        trigger.cancel();

        verify(task).cancel();
    }

    @Test
    public void primeStartRunnableThatStopsCheckingOnceSourceNoLongerExists() {
        BukkitTask task = mock(BukkitTask.class);
        Deployer deployer = mock(Deployer.class);
        Entity entity = mock(Entity.class);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.exists()).thenReturn(false);

        ItemEffectContext context = new ItemEffectContext(deployer, entity, source);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.prime(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void primeStartsRunnableThatDoesNothingInCurrentCheckWhenNoEnemyTargetsAreInsideCheckingRange() {
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
        trigger.prime(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task, never()).cancel();
    }

    @Test
    public void primeStartsRunnableThatNotifiesObserversWhenEnemyTargetsAreInsideCheckingRange() {
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
        trigger.prime(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(observer).onActivate();
    }
}
