package nl.matsgemmeke.battlegrounds.item.shoot;

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

    private Procedure onCycleFinish;
    private Shootable item;

    @Before
    public void setUp() {
        onCycleFinish = () -> {};
        item = mock(Shootable.class);

        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getScheduler()).thenReturn(mock(BukkitScheduler.class));
    }

    @Test
    public void stopShootingWhenExceedingRoundAmount() {
        int amountOfShots = 2;

        when(item.canShoot()).thenReturn(true);

        AutomaticFireCycleRunnable runnable = spy(new AutomaticFireCycleRunnable(item, amountOfShots, onCycleFinish));
        doNothing().when(runnable).cancel();
        runnable.run();
        runnable.run();

        verify(runnable).cancel();
        verify(item, times(2)).shoot();
    }
}
