package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.TargetQuery;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import nl.matsgemmeke.battlegrounds.item.effect.EffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class HitscanLauncherTest {

    private static final List<GameSound> SHOT_SOUNDS = List.of();
    private static final ParticleEffect TRAJECTORY_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0.0, 0.0, 0.0, 0.0, null, null);

    private AudioEmitter audioEmitter;
    private CollisionDetector collisionDetector;
    private HitscanProperties properties;
    private Effect effect;
    private ParticleEffectSpawner particleEffectSpawner;
    private TargetFinder targetFinder;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        properties = new HitscanProperties(SHOT_SOUNDS, TRAJECTORY_PARTICLE_EFFECT);
        collisionDetector = mock(CollisionDetector.class);
        effect = mock(Effect.class);
        particleEffectSpawner = mock(ParticleEffectSpawner.class);
        targetFinder = mock(TargetFinder.class);
    }

    @Test
    public void launchProducesProjectileStepUntilCollisionIsDetected() {
        Entity entity = mock(Entity.class);
        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        World world = mock(World.class);
        Location direction = new Location(world, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        Location hitLocation = new Location(world, 0.0, 0.0, 1.0, 0.0f, 0.0f);
        Material hitBlockMaterial = Material.STONE;

        Block hitBlock = mock(Block.class);
        when(hitBlock.getType()).thenReturn(hitBlockMaterial);
        when(hitBlock.getWorld()).thenReturn(world);

        LaunchContext launchContext = new LaunchContext(entity, source, direction);

        when(collisionDetector.producesBlockCollisionAt(hitLocation)).thenReturn(true);
        when(targetFinder.containsTargets(any(TargetQuery.class))).thenReturn(false);
        when(world.getBlockAt(hitLocation)).thenReturn(hitBlock);

        HitscanLauncher launcher = new HitscanLauncher(particleEffectSpawner, properties, audioEmitter, collisionDetector, effect, targetFinder);
        launcher.launch(launchContext);

        verify(particleEffectSpawner).spawnParticleEffect(TRAJECTORY_PARTICLE_EFFECT, hitLocation);
        verify(world).playEffect(hitLocation, org.bukkit.Effect.STEP_SOUND, hitBlockMaterial);
    }

    @Test
    public void launchProducesProjectileStepUntilTargetsAreFound() {
        Entity entity = mock(Entity.class);
        ProjectileLaunchSource source = mock(ProjectileLaunchSource.class);
        World world = mock(World.class);
        Location direction = new Location(world, 0.0, 0.0, 0.0, 0.0f, 0.0f);
        Location hitLocation = new Location(world, 0.0, 0.0, 1.0, 0.0f, 0.0f);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getWorld()).thenReturn(world);

        LaunchContext launchContext = new LaunchContext(entity, source, direction);

        when(collisionDetector.producesBlockCollisionAt(any(Location.class))).thenReturn(false);
        when(targetFinder.containsTargets(any(TargetQuery.class))).thenReturn(false, true);
        when(world.getBlockAt(hitLocation)).thenReturn(hitBlock);

        HitscanLauncher launcher = new HitscanLauncher(particleEffectSpawner, properties, audioEmitter, collisionDetector, effect, targetFinder);
        launcher.launch(launchContext);

        ArgumentCaptor<EffectContext> effectContextCaptor = ArgumentCaptor.forClass(EffectContext.class);
        verify(effect).prime(effectContextCaptor.capture());

        EffectContext effectContext = effectContextCaptor.getValue();
        assertThat(effectContext.getEntity()).isEqualTo(entity);
        assertThat(effectContext.getInitiationLocation()).isEqualTo(direction);
        assertThat(effectContext.getSource().getLocation()).isEqualTo(hitLocation);

        verify(effect).activateInstantly();
        verify(particleEffectSpawner).spawnParticleEffect(TRAJECTORY_PARTICLE_EFFECT, hitLocation);
    }
}
