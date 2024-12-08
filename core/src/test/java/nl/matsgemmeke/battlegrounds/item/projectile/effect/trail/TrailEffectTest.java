package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TrailEffectTest {

    private static final double EXTRA = 0.0;
    private static final double OFFSET_X = 0.1;
    private static final double OFFSET_Y = 0.1;
    private static final double OFFSET_Z = 0.1;
    private static final int COUNT = 1;
    private static final long CHECK_DELAY = 2;
    private static final long CHECK_PERIOD = 5;
    private static final Particle TYPE = Particle.FLAME;

    private TaskRunner taskRunner;
    private TrailProperties properties;

    @BeforeEach
    public void setUp() {
        taskRunner = mock(TaskRunner.class);

        ParticleEffectProperties particleEffect = new ParticleEffectProperties(TYPE, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA);

        properties = new TrailProperties(particleEffect, CHECK_DELAY, CHECK_PERIOD);
    }

    @Test
    public void onLaunchStopsCheckOnceProjectileNoLongerExists() {
        World world = mock(World.class);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(false);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        TrailEffect effect = new TrailEffect(taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(task).cancel();
        verifyNoInteractions(world);
    }

    @Test
    public void onLaunchSpawnsParticleAtProjectileLocation() {
        Location projectileLocation = new Location(null, 0, 0, 0);
        World world = mock(World.class);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(CHECK_DELAY), eq(CHECK_PERIOD))).thenReturn(task);

        TrailEffect effect = new TrailEffect(taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(CHECK_DELAY), eq(CHECK_PERIOD));

        runnableCaptor.getValue().run();

        verify(task, never()).cancel();
        verify(world).spawnParticle(TYPE, projectileLocation, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA);
    }
}
