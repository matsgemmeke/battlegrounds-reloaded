package com.github.matsgemmeke.battlegounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.mechanics.AutomaticFireCycleRunnable;
import com.github.matsgemmeke.battlegrounds.item.mechanics.FullyAutomaticMode;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class FullyAutomaticModeTest {

    private Firearm firearm;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.taskRunner = mock(TaskRunner.class);
        this.firearm = mock(Firearm.class);
    }

    @Test
    public void activatesWithCorrectDelayAndPeriod() {
        BattleItemHolder holder = mock(BattleItemHolder.class);

        int rateOfFire = 1200;

        FullyAutomaticMode fireMode = new FullyAutomaticMode(taskRunner, firearm, rateOfFire);
        fireMode.activate(holder);

        verify(taskRunner, times(1)).runTaskTimer(any(AutomaticFireCycleRunnable.class), eq(0L), eq(1L));
    }

    @Test
    public void cancelingResetsOperatingMode() {
        int rateOfFire = 600;

        FullyAutomaticMode fireMode = new FullyAutomaticMode(taskRunner, firearm, rateOfFire);
        fireMode.cancel();

        verify(firearm, times(1)).setCurrentOperatingMode(null);
    }

    @Test
    public void cancelingStopsCurrentCycle() {
        BattleItemHolder holder = mock(BattleItemHolder.class);
        BukkitTask task = mock(BukkitTask.class);

        when(taskRunner.runTaskTimer(any(BukkitRunnable.class), anyLong(), anyLong())).thenReturn(task);

        int rateOfFire = 600;

        FullyAutomaticMode fireMode = new FullyAutomaticMode(taskRunner, firearm, rateOfFire);
        fireMode.activate(holder);
        fireMode.cancel();

        verify(task, times(1)).cancel();
    }
}
