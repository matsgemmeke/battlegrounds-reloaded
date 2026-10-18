package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.HitboxTargetCondition;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HitscanLauncherTest {

    private static final ParticleEffect TRAJECTORY_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0.0, 0.0, 0.0, 0.0, null, null);
    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);
    private static final String ITEM_NAME = "Test Item";
    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();

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
    private SoundEmitter soundEmitter;
    @Mock
    private TargetFinder targetFinder;

    @Test
    @DisplayName("cancel stops ongoing sound play schedules")
    void cancel_stopsSoundPlay() {
        World world = mock(World.class);
        Location launchLocation = new Location(world, 1, 1, 1);
        SoundPlayback playback = mock(SoundPlayback.class);

        HitscanProperties properties = new HitscanProperties(SOUND_SEQUENCE, TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(ITEM_NAME, damageSource, projectileSource, launchLocation, () -> launchLocation, world);

        when(soundEmitter.playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>any())).thenReturn(playback);

        HitscanLauncher hitscanLauncher = new HitscanLauncher(collisionDetector, particleEffectSpawner, soundEmitter, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);
        hitscanLauncher.cancel();

        verify(playback).cancel();
    }

    @Test
    @DisplayName("launch produces projectile steps and starts item effect when collision is detected")
    void launch_startsItemEffectOnCollision() {
        World world = mock(World.class);
        Location launchLocation = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        Location hitLocation = new Location(world, 1.0, 1.0, 1.6, 0.0f, 0.0f);
        Material hitBlockMaterial = Material.STONE;

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(hitBlockMaterial);
        when(hitBlock.getWorld()).thenReturn(world);

        HitscanProperties properties = new HitscanProperties(SOUND_SEQUENCE, TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(ITEM_NAME, damageSource, projectileSource, launchLocation, () -> launchLocation, world);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(collisionDetector.producesBlockCollisionAt(hitLocation)).thenReturn(true);
        when(targetFinder.findTargets(any(TargetQuery.class))).thenReturn(List.of());
        when(world.getBlockAt(hitLocation)).thenReturn(hitBlock);

        HitscanLauncher hitscanLauncher = new HitscanLauncher(collisionDetector, particleEffectSpawner, soundEmitter, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getItemName()).isEqualTo(ITEM_NAME);
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
            assertThat(itemEffectContext.getStartingLocation()).isEqualTo(launchLocation);
        });

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>any());
        verify(particleEffectSpawner, times(1)).spawnParticleEffect(eq(TRAJECTORY_PARTICLE_EFFECT), any(Location.class));
        verify(world).playEffect(hitLocation, org.bukkit.Effect.STEP_SOUND, hitBlockMaterial);
    }

    @Test
    @DisplayName("launch produces projectile steps and starts item effect when a damage target is hit")
    void launch_startsItemEffectOnDamageTargetHit() {
        World world = mock(World.class);
        Location launchLocation = new Location(world, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        Location hitLocation = new Location(world, 1.0, 1.0, 1.6, 0.0f, 0.0f);
        DamageTarget hitTarget = mock(DamageTarget.class);

        HitscanProperties properties = new HitscanProperties(SOUND_SEQUENCE, TRAJECTORY_PARTICLE_EFFECT);
        LaunchContext launchContext = new LaunchContext(ITEM_NAME, damageSource, projectileSource, launchLocation, () -> launchLocation, world);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(targetFinder.findTargets(any(TargetQuery.class))).thenReturn(List.of()).thenReturn(List.of(hitTarget));

        HitscanLauncher hitscanLauncher = new HitscanLauncher(collisionDetector, particleEffectSpawner, soundEmitter, targetFinder, properties, itemEffect);
        hitscanLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getItemName()).isEqualTo(ITEM_NAME);
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
            assertThat(itemEffectContext.getStartingLocation()).isEqualTo(launchLocation);
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

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>any());
        verify(particleEffectSpawner, times(1)).spawnParticleEffect(eq(TRAJECTORY_PARTICLE_EFFECT), any(Location.class));
    }
}
