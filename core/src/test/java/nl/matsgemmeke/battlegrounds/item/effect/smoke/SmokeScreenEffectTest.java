package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class SmokeScreenEffectTest {

    private static final double PARTICLE_EXTRA = 0.01;
    private static final double PARTICLE_OFFSET_X = 0.5;
    private static final double PARTICLE_OFFSET_Y = 0.5;
    private static final double PARTICLE_OFFSET_Z = 0.5;
    private static final int PARTICLE_COUNT = 1;
    private static final List<GameSound> ACTIVATION_SOUNDS = Collections.emptyList();
    private static final long GROWTH_INTERVAL = 1L;
    private static final Particle PARTICLE_TYPE = Particle.CAMPFIRE_SIGNAL_SMOKE;

    private AudioEmitter audioEmitter;
    private CollisionDetector collisionDetector;
    private Deployer deployer;
    private Entity entity;
    private ItemEffectContext context;
    private ItemEffectSource source;
    private ParticleEffect particleEffect;
    private TaskRunner taskRunner;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        collisionDetector = mock(CollisionDetector.class);
        particleEffect = new ParticleEffect(PARTICLE_TYPE, PARTICLE_COUNT, PARTICLE_OFFSET_X, PARTICLE_OFFSET_Y, PARTICLE_OFFSET_Z, PARTICLE_EXTRA, null);
        taskRunner = mock(TaskRunner.class);
        trigger = mock(Trigger.class);

        deployer = mock(Deployer.class);
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        context = new ItemEffectContext(deployer, entity, source);
    }

    @Test
    public void activateCancelsTaskOnceSourceNoLongerExists() {
        SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, ACTIVATION_SOUNDS, 200L, 100L, 1.0, 0.0, 0.0, 0.0, GROWTH_INTERVAL);

        when(source.exists()).thenReturn(false);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(GROWTH_INTERVAL))).thenReturn(task);

        SmokeScreenEffect effect = new SmokeScreenEffect(taskRunner, properties, audioEmitter, collisionDetector);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_INTERVAL));

        runnableCaptor.getValue().run();

        verify(task).cancel();
    }

    @Test
    public void activateRemovesSourceAndCancelsTaskOnceEffectIsOver() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 0, 0, 0);

        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(GROWTH_INTERVAL))).thenReturn(task);

        SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, ACTIVATION_SOUNDS, 1L, 1L, 1.0, 0.0, 0.0, 0.0, GROWTH_INTERVAL);

        SmokeScreenEffect effect = new SmokeScreenEffect(taskRunner, properties, audioEmitter, collisionDetector);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_INTERVAL));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, sourceLocation);
        verify(source).remove();
        verify(task).cancel();
    }

    @Test
    public void activateDisplaysTraceParticleIfTheSourceHasMoved() {
        World world = mock(World.class);
        Location sourceOldLocation = new Location(world, 0, 0, 0);
        Location sourceNewLocation = new Location(world, 1, 1, 1);

        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceOldLocation, sourceOldLocation, sourceNewLocation);
        when(source.getWorld()).thenReturn(world);

        SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, ACTIVATION_SOUNDS, 200L, 100L, 1.0, 0.0, 0.0, 0.0, GROWTH_INTERVAL);

        SmokeScreenEffect effect = new SmokeScreenEffect(taskRunner, properties, audioEmitter, collisionDetector);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_INTERVAL));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, sourceOldLocation);
        verify(world).spawnParticle(PARTICLE_TYPE, sourceNewLocation, PARTICLE_COUNT, PARTICLE_OFFSET_X, PARTICLE_OFFSET_Y, PARTICLE_OFFSET_Z, PARTICLE_EXTRA);
    }

    @Test
    public void activateDisplaysSphereParticlesIfTheSourceIsNotMoving() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 0, 0, 0);

        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, ACTIVATION_SOUNDS, 200L, 100L, 5.0, 5.0, 0.5,0.0, GROWTH_INTERVAL);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.hasLineOfSight(any(Location.class), any(Location.class))).thenReturn(true);

        SmokeScreenEffect effect = new SmokeScreenEffect(taskRunner, properties, audioEmitter, collisionDetector);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_INTERVAL));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, sourceLocation);
        verify(world, times(2)).spawnParticle(eq(PARTICLE_TYPE), any(Location.class), eq(0), anyDouble(), anyDouble(), anyDouble(), eq(PARTICLE_EXTRA));
    }

    @Test
    public void activateDoesNotDisplaySphereParticleIfTheParticleLocationCausesCollision() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 0, 0, 0);
        SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, ACTIVATION_SOUNDS, 200L, 100L, 5.0, 5.0, 0.5, 0.0, GROWTH_INTERVAL);

        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(true);

        SmokeScreenEffect effect = new SmokeScreenEffect(taskRunner, properties, audioEmitter, collisionDetector);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_INTERVAL));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, sourceLocation);
        verifyNoInteractions(world);
    }

    @Test
    public void activateDoesNotDisplaySphereParticleIfTheParticleLocationHasNoLineOfSightToSource() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 0, 0, 0);
        SmokeScreenProperties properties = new SmokeScreenProperties(particleEffect, ACTIVATION_SOUNDS, 200L, 100L, 5.0, 5.0, 0.5, 0.0, GROWTH_INTERVAL);

        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.hasLineOfSight(any(Location.class), any(Location.class))).thenReturn(false);

        SmokeScreenEffect effect = new SmokeScreenEffect(taskRunner, properties, audioEmitter, collisionDetector);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), eq(0L), eq(GROWTH_INTERVAL));

        runnableCaptor.getValue().run();

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, sourceLocation);
        verifyNoInteractions(world);
    }
}
