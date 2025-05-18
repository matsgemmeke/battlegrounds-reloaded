package nl.matsgemmeke.battlegrounds.item.effect.trigger.enemy;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.item.trigger.enemy.EnemyProximityTrigger;
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
    public void isActivatedReturnsFalseWhenNotActivated() {
        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        boolean activated = trigger.isActivated();

        assertThat(activated).isFalse();
    }

    @Test
    public void isActivatedReturnsTrueWhenActivated() {
        Entity entity = mock(Entity.class);
        TriggerTarget target = mock(TriggerTarget.class);
        TriggerContext context = new TriggerContext(entity, target);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.activate(context);
        boolean activated = trigger.isActivated();

        assertThat(activated).isTrue();
    }

    @Test
    public void deactivateDoesNotCancelTriggerCheckWhenNotActivated() {
        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);

        assertThatCode(trigger::deactivate).doesNotThrowAnyException();
    }

    @Test
    public void deactivateCancelsTriggerCheckWhenActivated() {
        TriggerTarget target = mock(TriggerTarget.class);
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        TriggerContext context = new TriggerContext(entity, target);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.activate(context);
        trigger.deactivate();

        verify(task).cancel();
    }

    @Test
    public void activateStartsRunnableThatStopsCheckingOnceSourceNoLongerExists() {
        BukkitTask task = mock(BukkitTask.class);
        Entity entity = mock(Entity.class);

        TriggerTarget target = mock(TriggerTarget.class);
        when(target.exists()).thenReturn(false);

        TriggerContext context = new TriggerContext(entity, target);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void activateStartsRunnableThatDoesNothingInCurrentCheckWhenNoEnemyTargetsAreInsideCheckingRange() {
        BukkitTask task = mock(BukkitTask.class);
        Location targetLocation = new Location(null, 1, 1, 1);
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        TriggerTarget target = mock(TriggerTarget.class);
        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);

        TriggerContext context = new TriggerContext(entity, target);

        when(targetFinder.findEnemyTargets(entityId, targetLocation, CHECKING_RANGE)).thenReturn(Collections.emptyList());
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task, never()).cancel();
    }

    @Test
    public void activateStartsRunnableThatNotifiesObserversWhenEnemyTargetsAreInsideCheckingRange() {
        BukkitTask task = mock(BukkitTask.class);
        Location targetLocation = new Location(null, 1, 1, 1);
        TriggerObserver observer = mock(TriggerObserver.class);
        UUID entityId = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityId);

        TriggerTarget target = mock(TriggerTarget.class);
        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);

        TriggerContext context = new TriggerContext(entity, target);

        when(targetFinder.findEnemyTargets(entityId, targetLocation, CHECKING_RANGE)).thenReturn(List.of(mock(GameEntity.class)));
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(taskRunner, targetFinder, CHECKING_RANGE, PERIOD_BETWEEN_CHECKS);
        trigger.addObserver(observer);
        trigger.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(observer).onActivate();
    }
}
