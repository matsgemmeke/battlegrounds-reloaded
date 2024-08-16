package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BurstModeTest {

    private int rateOfFire;
    private int shotsAmount;
    private Shootable item;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        item = mock(Shootable.class);
        taskRunner = mock(TaskRunner.class);
        rateOfFire = 600;
        shotsAmount = 3;
    }

    @Test
    public void activatesWithCorrectDelayAndPeriod() {
        BurstMode fireMode = new BurstMode(item, taskRunner, shotsAmount, rateOfFire);
        fireMode.activateCycle();

        verify(taskRunner).runTaskTimer(any(BukkitRunnable.class), eq(0L), eq(2L));
    }

    @Test
    public void shouldNotCancelIfNotActivated() {
        BurstMode fireMode = new BurstMode(item, taskRunner, shotsAmount, rateOfFire);
        boolean cancelled = fireMode.cancelCycle();

        assertFalse(cancelled);
    }

    @Test
    public void cancelingStopsCurrentCycle() {
        BukkitTask task = mock(BukkitTask.class);

        when(taskRunner.runTaskTimer(any(BukkitRunnable.class), anyLong(), anyLong())).thenReturn(task);

        BurstMode fireMode = new BurstMode(item, taskRunner, shotsAmount, rateOfFire);
        fireMode.activateCycle();
        boolean cancelled = fireMode.cancelCycle();

        assertTrue(cancelled);

        verify(task).cancel();
    }

    @Test
    public void shouldNotBeCyclingIfNotActivated() {
        BurstMode fireMode = new BurstMode(item, taskRunner, shotsAmount, rateOfFire);

        assertFalse(fireMode.isCycling());
    }

    @Test
    public void shouldBeCyclingIfActivated() {
        BukkitTask task = mock(BukkitTask.class);

        when(taskRunner.runTaskTimer(any(BukkitRunnable.class), anyLong(), anyLong())).thenReturn(task);

        BurstMode fireMode = new BurstMode(item, taskRunner, shotsAmount, rateOfFire);
        fireMode.activateCycle();

        assertTrue(fireMode.isCycling());
    }
}
