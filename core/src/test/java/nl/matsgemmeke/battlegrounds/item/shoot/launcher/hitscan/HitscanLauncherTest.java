package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.HitboxTargetCondition;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HitscanLauncherTest {

    private static final Location LAUNCH_DIRECTION = new Location(null, 1, 1, 1);
    private static final long GAME_SOUND_DELAY = 5L;
    private static final ParticleEffect TRAJECTORY_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0.0, 0.0, 0.0, 0.0, null, null);
    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private CollisionDetector collisionDetector;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ParticleEffectSpawner particleEffectSpawner;
    @Mock
    private ProjectileLaunchSource projectileSource;
    @Mock
    private Scheduler scheduler;
    @Mock
    private TargetFinder targetFinder;

    @Test
    @DisplayName("cancel stops ongoing sound play schedules")
    void cancel_stopsSoundPlay() {
        World world = mock(World.class);
        Location direction = new Location(world, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        Schedule soundPlaySchedule = mock(Schedule.class);

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        HitscanProperties properties = new HitscanProperties(List.of(gameSound), TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);

        HitscanLauncher hitscanLauncher = new HitscanLauncher(audioEmitter, collisionDetector, particleEffectSpawner, scheduler, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);
        hitscanLauncher.cancel();

        verify(soundPlaySchedule).stop();
    }

    @Test
    @DisplayName("launch produces projectile steps and starts item effect when collision is detected")
    void launch_startsItemEffectOnCollision() {
        World world = mock(World.class);
        Location direction = new Location(world, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        Location hitLocation = new Location(world, 0.0, 0.0, 0.6, 0.0f, 0.0f);
        Material hitBlockMaterial = Material.STONE;

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(hitBlockMaterial);
        when(hitBlock.getWorld()).thenReturn(world);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        HitscanProperties properties = new HitscanProperties(List.of(gameSound), TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.producesBlockCollisionAt(hitLocation)).thenReturn(true);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        when(targetFinder.findTargets(any(TargetQuery.class))).thenReturn(List.of());
        when(world.getBlockAt(hitLocation)).thenReturn(hitBlock);

        HitscanLauncher hitscanLauncher = new HitscanLauncher(audioEmitter, collisionDetector, particleEffectSpawner, scheduler, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getActor()).satisfies(actor -> {
                assertThat(actor.getLocation()).isEqualTo(hitLocation);
                assertThat(actor.getWorld()).isEqualTo(world);
            });
            assertThat(itemEffectContext.getCollisionResult()).satisfies(collisionResult -> {
               assertThat(collisionResult.getHitBlock()).hasValue(hitBlock);
               assertThat(collisionResult.getHitTarget()).isEmpty();
               assertThat(collisionResult.getHitLocation()).hasValue(hitLocation);
            });
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(hitLocation);
        });

        verify(audioEmitter).playSound(gameSound, LAUNCH_DIRECTION);
        verify(particleEffectSpawner, times(1)).spawnParticleEffect(eq(TRAJECTORY_PARTICLE_EFFECT), any(Location.class));
        verify(world).playEffect(hitLocation, org.bukkit.Effect.STEP_SOUND, hitBlockMaterial);
    }

    @Test
    @DisplayName("launch produces projectile steps and starts item effect when a damage target is hit")
    void launch_startsItemEffectOnDamageTargetHit() {
        World world = mock(World.class);
        Location direction = new Location(world, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        Location hitLocation = new Location(world, 0.0, 0.0, 0.6, 0.0f, 0.0f);
        DamageTarget hitTarget = mock(DamageTarget.class);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        HitscanProperties properties = new HitscanProperties(List.of(gameSound), TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        when(targetFinder.findTargets(any(TargetQuery.class))).thenReturn(List.of()).thenReturn(List.of(hitTarget));

        HitscanLauncher hitscanLauncher = new HitscanLauncher(audioEmitter, collisionDetector, particleEffectSpawner, scheduler, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getActor()).satisfies(actor -> {
               assertThat(actor.getLocation()).isEqualTo(hitLocation);
               assertThat(actor.getWorld()).isEqualTo(world);
            });
            assertThat(itemEffectContext.getCollisionResult()).satisfies(collisionResult -> {
               assertThat(collisionResult.getHitBlock()).isEmpty();
               assertThat(collisionResult.getHitTarget()).hasValue(hitTarget);
               assertThat(collisionResult.getHitLocation()).hasValue(hitLocation);
            });
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(hitLocation);
        });

        ArgumentCaptor<TargetQuery> targetQueryCaptor = ArgumentCaptor.forClass(TargetQuery.class);
        verify(targetFinder, times(2)).findTargets(targetQueryCaptor.capture());

        assertThat(targetQueryCaptor.getValue()).satisfies(query -> {
            assertThat(query.getUniqueId()).hasValue(DAMAGE_SOURCE_ID);
            assertThat(query.getLocation()).hasValue(hitLocation);
            assertThat(query.getConditions()).satisfiesExactly(condition -> {
                assertThat(condition).isInstanceOf(HitboxTargetCondition.class);
            });
        });

        verify(audioEmitter).playSound(gameSound, LAUNCH_DIRECTION);
        verify(particleEffectSpawner, times(1)).spawnParticleEffect(eq(TRAJECTORY_PARTICLE_EFFECT), any(Location.class));
    }
}
