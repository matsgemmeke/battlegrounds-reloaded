package com.github.matsgemmeke.battlegounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.mechanics.BurstMode;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BurstModeTest {

    private BattleItemHolder holder;
    private Firearm firearm;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.holder = mock(BattleItemHolder.class);
        this.taskRunner = mock(TaskRunner.class);
        this.firearm = mock(Firearm.class);
    }

    @Test
    public void activatesWithCorrectDelayAndPeriod() {
        int roundAmount = 3;
        int rateOfFire = 600;

        BurstMode fireMode = new BurstMode(taskRunner, firearm, roundAmount, rateOfFire);
        fireMode.activate(holder);

        verify(taskRunner, times(1)).runTaskTimer(any(BukkitRunnable.class), eq(0L), eq(2L));
    }

    @Test
    public void cancelingResetsOperatingMode() {
        int roundAmount = 3;
        int rateOfFire = 600;

        BurstMode fireMode = new BurstMode(taskRunner, firearm, roundAmount, rateOfFire);
        fireMode.cancel(holder);

        verify(firearm, times(1)).setCurrentOperatingMode(null);
    }

    @Test
    public void cancelingStopsCurrentCycle() {
        BattleItemHolder holder = mock(BattleItemHolder.class);
        BukkitTask task = mock(BukkitTask.class);

        when(taskRunner.runTaskTimer(any(BukkitRunnable.class), anyLong(), anyLong())).thenReturn(task);

        int roundAmount = 3;
        int rateOfFire = 600;

        BurstMode fireMode = new BurstMode(taskRunner, firearm, roundAmount, rateOfFire);
        fireMode.activate(holder);
        fireMode.cancel(holder);

        verify(task, times(1)).cancel();
    }
}
