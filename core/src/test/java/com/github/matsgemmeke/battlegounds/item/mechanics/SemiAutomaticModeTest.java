package com.github.matsgemmeke.battlegounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.mechanics.SemiAutomaticMode;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SemiAutomaticModeTest {

    private BattleItemHolder holder;
    private Firearm firearm;
    private long cooldown;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.holder = mock(BattleItemHolder.class);
        this.taskRunner = mock(TaskRunner.class);
        this.firearm = mock(Firearm.class);
        this.cooldown = 1;
    }

    @Test
    public void activatingMakesGunShootAndSetOperatingMode() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(taskRunner, firearm, cooldown);
        fireMode.activate(holder);

        verify(firearm, times(1)).setCurrentOperatingMode(fireMode);
        verify(firearm, times(1)).shoot();
    }

    @Test
    public void cancelingResetsOperatingMode() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(taskRunner, firearm, cooldown);
        fireMode.cancel(holder);

        verify(firearm, times(1)).setCurrentOperatingMode(null);
    }

    @Test
    public void cancelingStopsCooldown() {
        BukkitTask task = mock(BukkitTask.class);

        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(task);

        SemiAutomaticMode fireMode = new SemiAutomaticMode(taskRunner, firearm, cooldown);
        fireMode.activate(holder);
        fireMode.cancel(holder);

        verify(firearm, times(1)).setCurrentOperatingMode(null);
        verify(task, times(1)).cancel();
    }
}
