package nl.matsgemmeke.battlegrounds.item.effect.activation.trigger;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class EnemyProximityTriggerTest {

    private double checkingRange;
    private long periodBetweenChecks;
    private TargetFinder targetFinder;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        checkingRange = 2.5;
        periodBetweenChecks = 5L;
        targetFinder = mock(TargetFinder.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void stopCheckOnceObjectIsRemoved() {
        BukkitTask task = mock(BukkitTask.class);
        ItemHolder holder = mock(ItemHolder.class);

        Deployable object = mock(Deployable.class);
        when(object.exists()).thenReturn(false);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(periodBetweenChecks))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(targetFinder, taskRunner, checkingRange, periodBetweenChecks);
        trigger.checkTriggerActivation(holder, object);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void doNothingInCurrentCheckWhenNoEnemyTargetsAreInsideCheckingRange() {
        BukkitTask task = mock(BukkitTask.class);
        ItemHolder holder = mock(ItemHolder.class);

        Location objectLocation = new Location(null, 1, 1, 1);

        Deployable object = mock(Deployable.class);
        when(object.exists()).thenReturn(true);
        when(object.getLocation()).thenReturn(objectLocation);

        when(targetFinder.findEnemyTargets(holder, objectLocation, checkingRange)).thenReturn(Collections.emptyList());
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(periodBetweenChecks))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(targetFinder, taskRunner, checkingRange, periodBetweenChecks);
        trigger.checkTriggerActivation(holder, object);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task, never()).cancel();
    }

    @Test
    public void notifyObserversWhenEnemyTargetsAreInsideCheckingRange() {
        BukkitTask task = mock(BukkitTask.class);
        ItemHolder holder = mock(ItemHolder.class);
        TriggerObserver observer = mock(TriggerObserver.class);

        Location objectLocation = new Location(null, 1, 1, 1);

        Deployable object = mock(Deployable.class);
        when(object.exists()).thenReturn(true);
        when(object.getLocation()).thenReturn(objectLocation);

        when(targetFinder.findEnemyTargets(holder, objectLocation, checkingRange)).thenReturn(List.of(mock(GameEntity.class)));
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(periodBetweenChecks))).thenReturn(task);

        EnemyProximityTrigger trigger = new EnemyProximityTrigger(targetFinder, taskRunner, checkingRange, periodBetweenChecks);
        trigger.addObserver(observer);
        trigger.checkTriggerActivation(holder, object);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(observer).onTrigger(holder, object);
        verify(task).cancel();
    }
}
