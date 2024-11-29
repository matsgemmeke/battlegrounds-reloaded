package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BounceEffectTest {

    private static final double BOUNCE_FACTOR = 0.7;
    private static final double VELOCITY_RETENTION = 0.9;
    private static final int AMOUNT_OF_BOUNCES = 1;
    private static final long CHECK_DELAY = 0L;
    private static final long CHECK_PERIOD = 1L;

    private BounceProperties properties;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        properties = new BounceProperties(AMOUNT_OF_BOUNCES, BOUNCE_FACTOR, VELOCITY_RETENTION, CHECK_DELAY, CHECK_PERIOD);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void onLaunchStopsCheckingOnceProjectileNoLongerExists() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD)));

        BounceEffect effect = new BounceEffect(taskRunner, properties);
        effect.onLaunch(projectile);

        verify(task).cancel();
    }
}
