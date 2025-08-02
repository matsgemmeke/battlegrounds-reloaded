package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import nl.matsgemmeke.battlegrounds.item.effect.EffectFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballProperties;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProjectileLauncherFactoryTest {
    
    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    
    private BulletLauncherFactory bulletLauncherFactory;
    private EffectFactory effectFactory;
    private FireballLauncherFactory fireballLauncherFactory;
    private GameContextProvider contextProvider;
    private ParticleEffectMapper particleEffectMapper;
    
    @BeforeEach
    public void setUp() {
        bulletLauncherFactory = mock(BulletLauncherFactory.class);
        effectFactory = mock(EffectFactory.class);
        fireballLauncherFactory = mock(FireballLauncherFactory.class);
        contextProvider = mock(GameContextProvider.class);
        particleEffectMapper = new ParticleEffectMapper();
    }

    @Test
    public void createReturnsInstanceOfFireballLauncher() {
        FireballLauncher fireballLauncher = mock(FireballLauncher.class);
        ProjectileSpec projectileSpec = this.createProjectileSpec("FIREBALL");
        Effect effect = mock(Effect.class);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        when(contextProvider.getComponent(GAME_KEY, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(GAME_KEY, CollisionDetector.class)).thenReturn(collisionDetector);
        when(contextProvider.getComponent(GAME_KEY, TargetFinder.class)).thenReturn(targetFinder);
        when(effectFactory.create(projectileSpec.effect, GAME_KEY)).thenReturn(effect);
        when(fireballLauncherFactory.create(any(FireballProperties.class), eq(audioEmitter), eq(collisionDetector), eq(effect), eq(targetFinder))).thenReturn(fireballLauncher);

        ProjectileLauncherFactory projectileLauncherFactory = new ProjectileLauncherFactory(bulletLauncherFactory, fireballLauncherFactory, contextProvider, effectFactory, particleEffectMapper);
        ProjectileLauncher createdProjectileLauncher = projectileLauncherFactory.create(projectileSpec, GAME_KEY);

        ArgumentCaptor<FireballProperties> fireballPropertiesCaptor = ArgumentCaptor.forClass(FireballProperties.class);
        verify(fireballLauncherFactory).create(fireballPropertiesCaptor.capture(), eq(audioEmitter), eq(collisionDetector), eq(effect), eq(targetFinder));

        FireballProperties fireballProperties = fireballPropertiesCaptor.getValue();
        assertThat(fireballProperties.shotSounds()).isEmpty();
        assertThat(fireballProperties.trajectoryParticleEffect()).satisfies(particleEffect -> {
            assertThat(particleEffect.particle()).isEqualTo(Particle.FLAME);
            assertThat(particleEffect.count()).isEqualTo(1);
            assertThat(particleEffect.offsetX()).isEqualTo(0.1);
            assertThat(particleEffect.offsetY()).isEqualTo(0.2);
            assertThat(particleEffect.offsetZ()).isEqualTo(0.3);
            assertThat(particleEffect.extra()).isEqualTo(0.0);
        });
        assertThat(fireballProperties.velocity()).isEqualTo(2.0);

        assertThat(createdProjectileLauncher).isEqualTo(fireballLauncher);
    }

    @Test
    public void createThrowsProjectileLauncherCreationExceptionWhenCreatingFireballLauncherWhileRequiredVariablesAreMissing() {
        ProjectileSpec projectileSpec = this.createProjectileSpec("FIREBALL");
        projectileSpec.velocity = null;

        ProjectileLauncherFactory projectileLauncherFactory = new ProjectileLauncherFactory(bulletLauncherFactory, fireballLauncherFactory, contextProvider, effectFactory, particleEffectMapper);

        assertThatThrownBy(() -> projectileLauncherFactory.create(projectileSpec, GAME_KEY))
                .isInstanceOf(ProjectileLauncherCreationException.class)
                .hasMessage("Cannot create projectile launcher for type FIREBALL because of invalid spec: Required 'velocity' value is missing");
    }
    
    @Test
    public void createReturnsInstanceOfBulletLauncher() {
        BulletLauncher bulletLauncher = mock(BulletLauncher.class);
        ProjectileSpec projectileSpec = this.createProjectileSpec("BULLET");
        Effect effect = mock(Effect.class);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        TargetFinder targetFinder = mock(TargetFinder.class);
        
        when(bulletLauncherFactory.create(any(BulletProperties.class), eq(audioEmitter), eq(collisionDetector), eq(effect), eq(targetFinder))).thenReturn(bulletLauncher);
        when(contextProvider.getComponent(GAME_KEY, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(GAME_KEY, CollisionDetector.class)).thenReturn(collisionDetector);
        when(contextProvider.getComponent(GAME_KEY, TargetFinder.class)).thenReturn(targetFinder);
        when(effectFactory.create(projectileSpec.effect, GAME_KEY)).thenReturn(effect);

        ProjectileLauncherFactory projectileLauncherFactory = new ProjectileLauncherFactory(bulletLauncherFactory, fireballLauncherFactory, contextProvider, effectFactory, particleEffectMapper);
        ProjectileLauncher createdProjectileLauncher = projectileLauncherFactory.create(projectileSpec, GAME_KEY);

        ArgumentCaptor<BulletProperties> bulletPropertiesCaptor = ArgumentCaptor.forClass(BulletProperties.class);
        verify(bulletLauncherFactory).create(bulletPropertiesCaptor.capture(), eq(audioEmitter), eq(collisionDetector), eq(effect), eq(targetFinder));
        
        BulletProperties bulletProperties = bulletPropertiesCaptor.getValue();
        assertThat(bulletProperties.shotSounds()).isEmpty();
        assertThat(bulletProperties.trajectoryParticleEffect()).satisfies(particleEffect -> {
            assertThat(particleEffect.particle()).isEqualTo(Particle.FLAME);
            assertThat(particleEffect.count()).isEqualTo(1);
            assertThat(particleEffect.offsetX()).isEqualTo(0.1);
            assertThat(particleEffect.offsetY()).isEqualTo(0.2);
            assertThat(particleEffect.offsetZ()).isEqualTo(0.3);
            assertThat(particleEffect.extra()).isEqualTo(0.0);
        });

        assertThat(createdProjectileLauncher).isEqualTo(bulletLauncher);
    }

    private ProjectileSpec createProjectileSpec(String type) {
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec();
        trajectoryParticleEffectSpec.particle = "FLAME";
        trajectoryParticleEffectSpec.count = 1;
        trajectoryParticleEffectSpec.offsetX = 0.1;
        trajectoryParticleEffectSpec.offsetY = 0.2;
        trajectoryParticleEffectSpec.offsetZ = 0.3;
        trajectoryParticleEffectSpec.extra = 0.0;

        ProjectileSpec projectileSpec = new ProjectileSpec();
        projectileSpec.type = type;
        projectileSpec.trajectoryParticleEffect = trajectoryParticleEffectSpec;
        projectileSpec.velocity = 2.0;
        return projectileSpec;
    }
}
