package nl.matsgemmeke.battlegrounds.item.effect.trigger.floor;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import nl.matsgemmeke.battlegrounds.item.trigger.floor.FloorHitTrigger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FloorHitTriggerTest {

    private static final long PERIOD_BETWEEN_CHECKS = 5L;

    private TaskRunner taskRunner;
    private TriggerContext context;
    private TriggerTarget target;

    @BeforeEach
    public void setUp() {
        taskRunner = mock(TaskRunner.class);
        target = mock(TriggerTarget.class);
        context = new TriggerContext(mock(Entity.class), target);
    }

    @Test
    public void isActivatedReturnsFalseWhenTriggerIsNotActivated() {
        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);
        boolean activated = trigger.isActivated();

        assertThat(activated).isFalse();
    }

    @Test
    public void isActivatedReturnsTrueWhenTriggerIsActivated() {
        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);
        trigger.activate(context);
        boolean activated = trigger.isActivated();

        assertThat(activated).isTrue();
    }

    @Test
    public void deactivateDoesNotCancelTriggerCheckWhenNotActivated() {
        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);

        assertThatCode(trigger::deactivate).doesNotThrowAnyException();
    }

    @Test
    public void deactivateCancelsTriggerCheck() {
        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);
        trigger.activate(context);
        trigger.deactivate();

        verify(task).cancel();
    }

    @Test
    public void activateStartsRunnableThatStopsCheckingOnceSourceNoLongerExists() {
        when(target.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);
        trigger.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void activateStartsRunnableThatNotifiesObserversOnceBlockBelowObjectIsNotPassable() {
        TriggerObserver observer = mock(TriggerObserver.class);
        World world = mock(World.class);
        Location targetLocation = new Location(world, 1, 1, 1);

        Block blockBelowObject = mock(Block.class);
        when(blockBelowObject.isPassable()).thenReturn(true).thenReturn(false);
        when(world.getBlockAt(any(Location.class))).thenReturn(blockBelowObject);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(PERIOD_BETWEEN_CHECKS))).thenReturn(task);

        FloorHitTrigger trigger = new FloorHitTrigger(taskRunner, PERIOD_BETWEEN_CHECKS);
        trigger.addObserver(observer);
        trigger.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();
        runnable.run();

        verify(observer).onActivate();
        verify(task).cancel();
    }
}
