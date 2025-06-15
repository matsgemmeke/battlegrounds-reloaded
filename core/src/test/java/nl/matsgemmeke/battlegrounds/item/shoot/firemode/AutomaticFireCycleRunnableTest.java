package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class AutomaticFireCycleRunnableTest {

    private MockedStatic<Bukkit> bukkit;
    private Procedure onCycleFinish;
    private Shootable item;

    @BeforeEach
    public void setUp() {
        onCycleFinish = () -> {};
        item = mock(Shootable.class);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getScheduler).thenReturn(mock(BukkitScheduler.class));
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
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
