package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.shoot.fullauto.FullyAutomaticMode;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class FullyAutomaticModeTest {

    private Shootable item;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        item = mock(Shootable.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void activatesWithCorrectDelayAndPeriod() {
        int rateOfFire = 1200;

        FullyAutomaticMode fireMode = new FullyAutomaticMode(taskRunner, item, rateOfFire);
        fireMode.activateCycle();

        verify(taskRunner).runTaskTimer(any(AutomaticFireCycleRunnable.class), eq(0L), eq(1L));
    }

    @Test
    public void shouldNotCancelIfNotActivated() {
        int rateOfFire = 600;

        FullyAutomaticMode fireMode = new FullyAutomaticMode(taskRunner, item, rateOfFire);
        boolean cancelled = fireMode.cancelCycle();

        assertFalse(cancelled);
    }

    @Test
    public void cancelingStopsCurrentCycle() {
        BukkitTask task = mock(BukkitTask.class);

        when(taskRunner.runTaskTimer(any(BukkitRunnable.class), anyLong(), anyLong())).thenReturn(task);

        int rateOfFire = 600;

        FullyAutomaticMode fireMode = new FullyAutomaticMode(taskRunner, item, rateOfFire);
        fireMode.activateCycle();
        boolean cancelled = fireMode.cancelCycle();

        assertTrue(cancelled);

        verify(task).cancel();
    }

    @Test
    public void shouldNotBeCyclingIfNotActivated() {
        int rateOfFire = 600;

        FullyAutomaticMode fireMode = new FullyAutomaticMode(taskRunner, item, rateOfFire);

        assertFalse(fireMode.isCycling());
    }

    @Test
    public void shouldBeCyclingIfActivated() {
        int rateOfFire = 600;

        BukkitTask task = mock(BukkitTask.class);

        when(taskRunner.runTaskTimer(any(BukkitRunnable.class), anyLong(), anyLong())).thenReturn(task);

        FullyAutomaticMode fireMode = new FullyAutomaticMode(taskRunner, item, rateOfFire);
        fireMode.activateCycle();

        assertTrue(fireMode.isCycling());
    }
}
