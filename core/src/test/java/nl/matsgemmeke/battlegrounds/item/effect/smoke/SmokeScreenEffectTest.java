package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ParticleSettings;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class SmokeScreenEffectTest {

    private static final double PARTICLE_EXTRA = 0.01;
    private static final double PARTICLE_OFFSET_X = 0.5;
    private static final double PARTICLE_OFFSET_Y = 0.5;
    private static final double PARTICLE_OFFSET_Z = 0.5;
    private static final int PARTICLE_COUNT = 1;
    private static final Iterable<GameSound> IGNITION_SOUNDS = Collections.emptyList();
    private static final long GROWTH_PERIOD = 1L;
    private static final Particle PARTICLE_TYPE = Particle.CAMPFIRE_SIGNAL_SMOKE;

    private AudioEmitter audioEmitter;
    private CollisionDetector collisionDetector;
    private ParticleSettings particleSettings;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        collisionDetector = mock(CollisionDetector.class);
        particleSettings = new ParticleSettings(PARTICLE_TYPE, PARTICLE_COUNT, PARTICLE_OFFSET_X, PARTICLE_OFFSET_Y, PARTICLE_OFFSET_Z, PARTICLE_EXTRA);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void activateRemovesSourceAndCancelsTaskOnceEffectIsOver() {
        int duration = 1;

        World world = mock(World.class);
        Location sourceLocation = new Location(world, 0, 0, 0);

        ItemHolder holder = mock(ItemHolder.class);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(GROWTH_PERIOD))).thenReturn(task);

        ItemEffectContext context = new ItemEffectContext(holder, source);
        SmokeScreenSettings smokeScreenSettings = new SmokeScreenSettings(IGNITION_SOUNDS, duration, 1.0, 0.0, 0.0, 0.0, GROWTH_PERIOD);

        SmokeScreenEffect effect = new SmokeScreenEffect(smokeScreenSettings, particleSettings, audioEmitter, collisionDetector, taskRunner);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_PERIOD));

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(audioEmitter).playSounds(IGNITION_SOUNDS, sourceLocation);
        verify(source).remove();
        verify(task).cancel();
    }

    @Test
    public void activateDisplaysTraceParticleIfTheSourceHasMoved() {
        World world = mock(World.class);
        Location sourceOldLocation = new Location(world, 0, 0, 0);
        Location sourceNewLocation = new Location(world, 1, 1, 1);

        ItemHolder holder = mock(ItemHolder.class);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceOldLocation, sourceOldLocation, sourceNewLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);
        SmokeScreenSettings smokeScreenSettings = new SmokeScreenSettings(IGNITION_SOUNDS, 100, 1.0, 0.0, 0.0, 0.0, GROWTH_PERIOD);

        SmokeScreenEffect effect = new SmokeScreenEffect(smokeScreenSettings, particleSettings, audioEmitter, collisionDetector, taskRunner);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_PERIOD));

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(audioEmitter).playSounds(IGNITION_SOUNDS, sourceOldLocation);
        verify(world).spawnParticle(PARTICLE_TYPE, sourceNewLocation, PARTICLE_COUNT, PARTICLE_OFFSET_X, PARTICLE_OFFSET_Y, PARTICLE_OFFSET_Z, PARTICLE_EXTRA);
    }

    @Test
    public void activateDisplaysSphereParticlesIfTheSourceIsNotMoving() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 0, 0, 0);

        ItemHolder holder = mock(ItemHolder.class);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);
        SmokeScreenSettings smokeScreenSettings = new SmokeScreenSettings(IGNITION_SOUNDS, 100, 5.0, 5.0, 0.5,0.0, GROWTH_PERIOD);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.hasLineOfSight(any(Location.class), any(Location.class))).thenReturn(true);

        SmokeScreenEffect effect = new SmokeScreenEffect(smokeScreenSettings, particleSettings, audioEmitter, collisionDetector, taskRunner);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_PERIOD));

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(audioEmitter).playSounds(IGNITION_SOUNDS, sourceLocation);
        verify(world, times(2)).spawnParticle(eq(PARTICLE_TYPE), any(Location.class), eq(0), anyDouble(), anyDouble(), anyDouble(), eq(PARTICLE_EXTRA));
    }

    @Test
    public void activateDoesNotDisplaySphereParticleIfTheParticleLocationCausesCollision() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 0, 0, 0);

        ItemHolder holder = mock(ItemHolder.class);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);
        SmokeScreenSettings smokeScreenSettings = new SmokeScreenSettings(IGNITION_SOUNDS, 100, 5.0, 5.0, 0.5, 0.0, GROWTH_PERIOD);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(true);

        SmokeScreenEffect effect = new SmokeScreenEffect(smokeScreenSettings, particleSettings, audioEmitter, collisionDetector, taskRunner);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_PERIOD));

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(audioEmitter).playSounds(IGNITION_SOUNDS, sourceLocation);
        verifyNoInteractions(world);
    }

    @Test
    public void activateDoesNotDisplaySphereParticleIfTheParticleLocationHasNoLineOfSightToSource() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 0, 0, 0);

        ItemHolder holder = mock(ItemHolder.class);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);
        SmokeScreenSettings smokeScreenSettings = new SmokeScreenSettings(IGNITION_SOUNDS, 100, 5.0, 5.0, 0.5, 0.0, GROWTH_PERIOD);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.hasLineOfSight(any(Location.class), any(Location.class))).thenReturn(false);

        SmokeScreenEffect effect = new SmokeScreenEffect(smokeScreenSettings, particleSettings, audioEmitter, collisionDetector, taskRunner);
        effect.activate(context);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_PERIOD));

        Runnable runnable = runnableCaptor.getValue();
        runnable.run();

        verify(audioEmitter).playSounds(IGNITION_SOUNDS, sourceLocation);
        verifyNoInteractions(world);
    }
}
