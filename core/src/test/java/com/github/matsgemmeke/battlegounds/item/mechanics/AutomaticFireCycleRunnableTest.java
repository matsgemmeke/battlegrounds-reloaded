package com.github.matsgemmeke.battlegounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.mechanics.AutomaticFireCycleRunnable;
import com.github.matsgemmeke.battlegrounds.item.mechanics.Procedure;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class AutomaticFireCycleRunnableTest {

    private Firearm firearm;
    private Procedure onCycleFinish;

    @Before
    public void setUp() {
        this.firearm = mock(Firearm.class);
        this.onCycleFinish = () -> {};

        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getScheduler()).thenReturn(mock(BukkitScheduler.class));
    }

    @Test
    public void stopShootingWhenExceedingRoundAmount() {
        int amountOfShots = 2;

        when(firearm.getMagazineAmmo()).thenReturn(10);

        AutomaticFireCycleRunnable runnable = spy(new AutomaticFireCycleRunnable(firearm, amountOfShots, onCycleFinish));
        doNothing().when(runnable).cancel();
        runnable.run();
        runnable.run();

        verify(runnable, times(1)).cancel();
        verify(firearm, times(2)).shoot();
    }
}
