package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FireballLauncherTest {

    private static final double VELOCITY = 2.0;
    private static final List<GameSound> SHOT_SOUNDS = List.of();
    private static final ParticleEffect TRAJECTORY_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0.0, 0.0, 0.0, 0.0, null, null);

    private AudioEmitter audioEmitter;
    private FireballProperties properties;
    private ItemEffect itemEffect;
    private ParticleEffectSpawner particleEffectSpawner;
    private ProjectileHitActionRegistry projectileHitActionRegistry;
    private Scheduler scheduler;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        properties = new FireballProperties(SHOT_SOUNDS, TRAJECTORY_PARTICLE_EFFECT, VELOCITY);
        itemEffect = mock(ItemEffect.class);
        particleEffectSpawner = mock(ParticleEffectSpawner.class);
        projectileHitActionRegistry = mock(ProjectileHitActionRegistry.class);
        scheduler = mock(Scheduler.class);
    }

    @Test
    public void launchStartsScheduleThatActivatesEffectWhenProjectileHitActionGetsCalled() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        Location entityLocation = new Location(world, 10.0, 1.0, 1.0);
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

        doAnswer(invocation -> {
            ProjectileHitAction projectileHitAction = invocation.getArgument(1);
            projectileHitAction.onProjectileHit();
            return null;
        }).when(projectileHitActionRegistry).registerProjectileHitAction(eq(fireball), any(ProjectileHitAction.class));

        FireballLauncher launcher = new FireballLauncher(audioEmitter, particleEffectSpawner, projectileHitActionRegistry, scheduler, properties, itemEffect);
        launcher.launch(context);

        ArgumentCaptor<ScheduleTask> scheduleTaskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(scheduleTaskCaptor.capture());

        scheduleTaskCaptor.getValue().run();
        
        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        ItemEffectContext itemEffectContext = itemEffectContextCaptor.getValue();
        assertThat(itemEffectContext.getEntity()).isEqualTo(entity);
        assertThat(itemEffectContext.getSource().getLocation()).isEqualTo(fireballLocation);
        assertThat(itemEffectContext.getSource().getWorld()).isEqualTo(world);
        assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(entityLocation);

        verify(audioEmitter).playSounds(SHOT_SOUNDS, entityLocation);
        verify(particleEffectSpawner).spawnParticleEffect(TRAJECTORY_PARTICLE_EFFECT, fireballLocation);
        verify(schedule).stop();
    }
}
