package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TrailEffectTest {

    private static final double EXTRA = 0.0;
    private static final double OFFSET_X = 0.1;
    private static final double OFFSET_Y = 0.1;
    private static final double OFFSET_Z = 0.1;
    private static final int COUNT = 1;
    private static final int MAX_ACTIVATIONS = 2;
    private static final List<Long> INTERVALS = List.of(5L);
    private static final Long DELAY = 2L;
    private static final Particle PARTICLE = Particle.FLAME;
    private static final ParticleEffect PARTICLE_EFFECT = new ParticleEffect(PARTICLE, COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, null);

    private ParticleEffectSpawner particleEffectSpawner;
    private TaskRunner taskRunner;
    private TrailProperties properties;

    @BeforeEach
    public void setUp() {
        particleEffectSpawner = mock(ParticleEffectSpawner.class);
        taskRunner = mock(TaskRunner.class);
        properties = new TrailProperties(PARTICLE_EFFECT, DELAY, INTERVALS, MAX_ACTIVATIONS);
    }

    @Test
    public void onLaunchStopsecChkOnceProjectileNoLongerExists() {
        World world = mock(World.class);

        Projectile projectile = mock(Projectile.class);
        when(projectile.exists()).thenReturn(false);
        when(projectile.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(DELAY), eq(1L))).thenReturn(task);

        TrailEffect effect = new TrailEffect(particleEffectSpawner, taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(DELAY), eq(1L));

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
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(DELAY), eq(1L))).thenReturn(task);

        TrailEffect effect = new TrailEffect(particleEffectSpawner, taskRunner, properties);
        effect.onLaunch(projectile);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(DELAY), eq(1L));

        runnableCaptor.getValue().run();

        verify(task, never()).cancel();
        verify(particleEffectSpawner).spawnParticleEffect(PARTICLE_EFFECT, world, projectileLocation);
    }
}
