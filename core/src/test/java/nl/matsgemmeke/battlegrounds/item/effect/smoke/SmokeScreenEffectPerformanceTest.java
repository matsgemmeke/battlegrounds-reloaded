package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static nl.matsgemmeke.battlegrounds.ArgumentMatcherUtils.isBetween;
import static nl.matsgemmeke.battlegrounds.MockUtils.RUN_SCHEDULE_TASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmokeScreenEffectPerformanceTest {

    private static final double PARTICLE_EXTRA = 0.01;
    private static final double PARTICLE_OFFSET_X = 0.5;
    private static final double PARTICLE_OFFSET_Y = 0.5;
    private static final double PARTICLE_OFFSET_Z = 0.5;
    private static final int PARTICLE_COUNT = 1;
    private static final Material PARTICLE_BLOCK_DATA = null;
    private static final Particle PARTICLE_TYPE = Particle.CAMPFIRE_SIGNAL_SMOKE;
    private static final ParticleEffect PARTICLE_EFFECT = new ParticleEffect(PARTICLE_TYPE, PARTICLE_COUNT, PARTICLE_OFFSET_X, PARTICLE_OFFSET_Y, PARTICLE_OFFSET_Z, PARTICLE_EXTRA, PARTICLE_BLOCK_DATA, null);

    private static final List<GameSound> ACTIVATION_SOUNDS = Collections.emptyList();
    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final long MIN_DURATION = 10L;
    private static final long MAX_DURATION = 20L;
    private static final long GROWTH_INTERVAL = 1L;
    private static final double MIN_SIZE = 3.0;
    private static final double MAX_SIZE = 5.0;
    private static final SmokeScreenProperties PROPERTIES = new SmokeScreenProperties(PARTICLE_EFFECT, ACTIVATION_SOUNDS, MIN_DURATION, MAX_DURATION, 1.0, MIN_SIZE, MAX_SIZE, 0.0, GROWTH_INTERVAL);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private CollisionDetector collisionDetector;
    @Mock
    private DamageSource damageSource;
    @Mock(extraInterfaces = Removable.class)
    private ItemEffectSource effectSource;
    @Mock
    private Scheduler scheduler;
    @Mock
    private TriggerTarget triggerTarget;

    private SmokeScreenEffectPerformance performance;

    @BeforeEach
    void setUp() {
        performance = new SmokeScreenEffectPerformance(audioEmitter, collisionDetector, scheduler, PROPERTIES);
    }

    @Test
    void isPerformingReturnsFalseWhenNotPerforming() {
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void isPerformingReturnsTrueWhenPerforming() {
        Schedule cancelSchedule = mock(Schedule.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        Schedule repeatingSchedule = mock(Schedule.class);
        when(repeatingSchedule.isRunning()).thenReturn(true);

        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isTrue();
    }

    @Test
    void performCancelsRepeatingScheduleOnceSourceNoLongerExists() {
        Schedule cancelSchedule = mock(Schedule.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        when(effectSource.exists()).thenReturn(false);
        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);

        verify(repeatingSchedule).stop();
    }

    @Test
    void performDisplaysTraceParticleWhenSourceHasMoved() {
        Schedule cancelSchedule = mock(Schedule.class);
        World world = mock(World.class);
        Location effectSourceOldLocation = new Location(world, 0, 0, 0);
        Location effectSourceNewLocation = new Location(world, 1, 1, 1);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        when(effectSource.exists()).thenReturn(true);
        when(effectSource.getLocation()).thenReturn(effectSourceOldLocation, effectSourceNewLocation);
        when(effectSource.getWorld()).thenReturn(world);
        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, effectSourceOldLocation);
        verify(world).spawnParticle(PARTICLE_TYPE, effectSourceNewLocation, PARTICLE_COUNT, PARTICLE_OFFSET_X, PARTICLE_OFFSET_Y, PARTICLE_OFFSET_Z, PARTICLE_EXTRA);
    }

    @Test
    void performDisplaysSphereParticlesWhenSourceIsNotMoving() {
        World world = mock(World.class);
        Location effectSourceLocation = new Location(world, 0, 0, 0);
        Schedule cancelSchedule = mock(Schedule.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.hasLineOfSight(any(Location.class), any(Location.class))).thenReturn(true);
        when(effectSource.exists()).thenReturn(true);
        when(effectSource.getLocation()).thenReturn(effectSourceLocation);
        when(effectSource.getWorld()).thenReturn(world);
        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, effectSourceLocation);
        verify(world, times(3)).spawnParticle(eq(PARTICLE_TYPE), any(Location.class), eq(0), anyDouble(), anyDouble(), anyDouble(), eq(PARTICLE_EXTRA), eq(PARTICLE_BLOCK_DATA), eq(true));
    }

    @Test
    void performDoesNotDisplaySphereParticleIfTheParticleLocationCausesCollision() {
        World world = mock(World.class);
        Location effectSourceLocation = new Location(world, 0, 0, 0);
        Schedule cancelSchedule = mock(Schedule.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(true);
        when(effectSource.exists()).thenReturn(true);
        when(effectSource.getLocation()).thenReturn(effectSourceLocation);
        when(effectSource.getWorld()).thenReturn(world);
        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, effectSourceLocation);
        verifyNoInteractions(world);
    }

    @Test
    void performDoesNotDisplaySphereParticleIfTheParticleLocationHasNoLineOfSightToSource() {
        World world = mock(World.class);
        Location effectSourceLocation = new Location(world, 0, 0, 0);
        Schedule cancelSchedule = mock(Schedule.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.hasLineOfSight(any(Location.class), any(Location.class))).thenReturn(false);
        when(effectSource.exists()).thenReturn(true);
        when(effectSource.getLocation()).thenReturn(effectSourceLocation);
        when(effectSource.getWorld()).thenReturn(world);
        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, effectSourceLocation);
        verifyNoInteractions(world);
    }

    @Test
    void performRemovesSourceAndCancelsRepeatingScheduleOnceEffectIsOver() {
        Location effectSourceLocation = new Location(null, 0, 0, 0);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        Schedule repeatingSchedule = mock(Schedule.class);
        when(repeatingSchedule.isRunning()).thenReturn(true);

        Schedule cancelSchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(cancelSchedule).addTask(any(ScheduleTask.class));

        when(effectSource.getLocation()).thenReturn(effectSourceLocation);
        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, effectSourceLocation);
        verify(repeatingSchedule).stop();
        verify((Removable) effectSource).remove();
    }

    @Test
    void rollbackCancelsRepeatingSchedule() {
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);
        Schedule cancelSchedule = mock(Schedule.class);

        Schedule repeatingSchedule = mock(Schedule.class);
        when(repeatingSchedule.isRunning()).thenReturn(true);

        when(scheduler.createRepeatingSchedule(0L, GROWTH_INTERVAL)).thenReturn(repeatingSchedule);
        when(scheduler.createSingleRunSchedule(longThat(isBetween(MIN_DURATION, MAX_DURATION)))).thenReturn(cancelSchedule);

        performance.perform(context);
        performance.rollback();

        verify(repeatingSchedule).stop();
    }
}
