package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileRegistry;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FireballLauncherTest {

    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);
    private static final double VELOCITY = 2.0;
    private static final ParticleEffect TRAJECTORY_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0.0, 0.0, 0.0, 0.0, null, null);
    private static final UUID FIREBALL_UNIQUE_ID = UUID.randomUUID();

    private static final String ITEM_NAME = "Test Item";
    private static final Location LAUNCH_DIRECTION = new Location(null, 1, 1, 1);

    @Mock
    private CollisionResultAdapter collisionResultAdapter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ParticleEffectSpawner particleEffectSpawner;
    @Mock
    private ProjectileHitActionRegistry projectileHitActionRegistry;
    @Mock
    private ProjectileLaunchSource projectileSource;
    @Mock
    private ProjectileRegistry projectileRegistry;
    @Mock
    private Scheduler scheduler;
    @Mock
    private SoundEmitter soundEmitter;

    @Test
    @DisplayName("cancel stops ongoing sound playbacks")
    void cancel() {
        SmallFireball fireball = mock(SmallFireball.class);
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        SoundPlayback soundPlayback = mock(SoundPlayback.class);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        FireballProperties properties = new FireballProperties(SOUND_SEQUENCE, TRAJECTORY_PARTICLE_EFFECT, VELOCITY);
        LaunchContext context = new LaunchContext(ITEM_NAME, damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        when(soundEmitter.playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == LAUNCH_DIRECTION))).thenReturn(soundPlayback);
        when(projectileSource.launchProjectile(eq(SmallFireball.class), any(Vector.class))).thenReturn(fireball);
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);

        FireballLauncher launcher = new FireballLauncher(collisionResultAdapter, particleEffectSpawner, projectileHitActionRegistry, projectileRegistry, scheduler, soundEmitter, properties, itemEffect);
        launcher.launch(context);
        launcher.cancel();

        verify(soundPlayback).cancel();
    }

    @Test
    @DisplayName("launch starts schedule that activates effect when projectile hit action gets called")
    void launch() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        Location fireballLocation = new Location(world, 1.0, 1.0, 1.0);
        ProjectileHitResult projectileHitResult = new ProjectileHitResult(null, null);
        CollisionResult collisionResult = new CollisionResult(null, null, null);

        SmallFireball fireball = mock(SmallFireball.class);
        when(fireball.getLocation()).thenReturn(fireballLocation);
        when(fireball.getUniqueId()).thenReturn(FIREBALL_UNIQUE_ID);
        when(fireball.getWorld()).thenReturn(world);

        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        when(source.launchProjectile(eq(SmallFireball.class), any(Vector.class))).thenReturn(fireball);

        Schedule repeatingSchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        FireballProperties properties = new FireballProperties(SOUND_SEQUENCE, TRAJECTORY_PARTICLE_EFFECT, VELOCITY);
        LaunchContext context = new LaunchContext(ITEM_NAME, damageSource, source, direction, () -> LAUNCH_DIRECTION, world);

        when(collisionResultAdapter.adapt(projectileHitResult, fireball)).thenReturn(collisionResult);
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        doAnswer(MockUtils.answerRunProjectileHitAction(projectileHitResult)).when(projectileHitActionRegistry).registerProjectileHitAction(eq(fireball), any(ProjectileHitAction.class));

        FireballLauncher launcher = new FireballLauncher(collisionResultAdapter, particleEffectSpawner, projectileHitActionRegistry, projectileRegistry, scheduler, soundEmitter, properties, itemEffect);
        launcher.launch(context);
        
        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getItemName()).isEqualTo(ITEM_NAME);
            assertThat(itemEffectContext.getCollisionResult()).isEqualTo(collisionResult);
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getActor()).satisfies(actor -> {
                assertThat(actor.getLocation()).isEqualTo(fireballLocation);
                assertThat(actor.getWorld()).isEqualTo(world);
            });
            assertThat(itemEffectContext.getStartingLocation()).isEqualTo(direction);
        });

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == LAUNCH_DIRECTION));
        verify(particleEffectSpawner).spawnParticleEffect(TRAJECTORY_PARTICLE_EFFECT, fireballLocation);
        verify(projectileHitActionRegistry).removeProjectileHitAction(fireball);
        verify(projectileRegistry).register(FIREBALL_UNIQUE_ID);
        verify(repeatingSchedule).stop();
    }
}
