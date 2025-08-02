package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.*;

public class FireballLauncherTest {

    private static final double VELOCITY = 2.0;
    private static final List<GameSound> SHOT_SOUNDS = List.of();
    private static final ParticleEffect TRAJECTORY_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0.0, 0.0, 0.0, 0.0, null, null);

    private AudioEmitter audioEmitter;
    private CollisionDetector collisionDetector;
    private Effect effect;
    private FireballProperties properties;
    private ParticleEffectSpawner particleEffectSpawner;
    private Scheduler scheduler;
    private TargetFinder targetFinder;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        collisionDetector = mock(CollisionDetector.class);
        effect = mock(Effect.class);
        properties = new FireballProperties(SHOT_SOUNDS, TRAJECTORY_PARTICLE_EFFECT, VELOCITY);
        particleEffectSpawner = mock(ParticleEffectSpawner.class);
        scheduler = mock(Scheduler.class);
        targetFinder = mock(TargetFinder.class);
    }

    @Test
    public void launchStartsScheduleThatActivatesEffectWhenProducingCollisionWithBlock() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        Location entityLocation = new Location(world, 1.0, 1.0, 1.0);
        Location fireballLocation = new Location(world, 1.0, 1.0, 1.0);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(entityLocation);

        SmallFireball fireball = mock(SmallFireball.class);
        when(fireball.getLocation()).thenReturn(fireballLocation);
        when(fireball.getWorld()).thenReturn(world);

        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        when(source.launchProjectile(eq(SmallFireball.class), any(Vector.class))).thenReturn(fireball);

        Schedule schedule = mock(Schedule.class);
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(schedule);

        LaunchContext context = new LaunchContext(entity, source, direction);

        FireballLauncher launcher = new FireballLauncher(particleEffectSpawner, scheduler, properties, audioEmitter, collisionDetector, effect, targetFinder);
        launcher.launch(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule, times(2)).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getAllValues().forEach(ScheduleTask::run);

        verify(audioEmitter).playSounds(SHOT_SOUNDS, entityLocation);
        verify(particleEffectSpawner).spawnParticleEffect(TRAJECTORY_PARTICLE_EFFECT, fireballLocation);
    }
}
