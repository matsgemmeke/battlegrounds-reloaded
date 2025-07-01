package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletProperties;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProjectileLauncherFactoryTest {
    
    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    
    private BulletLauncherFactory bulletLauncherFactory;
    private GameContextProvider contextProvider;
    private ParticleEffectMapper particleEffectMapper;
    
    @BeforeEach
    public void setUp() {
        bulletLauncherFactory = mock(BulletLauncherFactory.class);
        contextProvider = mock(GameContextProvider.class);
        particleEffectMapper = new ParticleEffectMapper();
    }
    
    @Test
    public void createReturnsInstanceOfBulletLauncher() {
        BulletLauncher bulletLauncher = mock(BulletLauncher.class);
        ProjectileSpec projectileSpec = this.createBulletProjectileSpec();

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        
        when(bulletLauncherFactory.create(any(BulletProperties.class), eq(audioEmitter), eq(collisionDetector))).thenReturn(bulletLauncher);
        when(contextProvider.getComponent(GAME_KEY, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(GAME_KEY, CollisionDetector.class)).thenReturn(collisionDetector);
        
        ProjectileLauncherFactory projectileLauncherFactory = new ProjectileLauncherFactory(bulletLauncherFactory, contextProvider, particleEffectMapper);
        ProjectileLauncher projectileLauncher = projectileLauncherFactory.create(projectileSpec, GAME_KEY);

        ArgumentCaptor<BulletProperties> bulletPropertiesCaptor = ArgumentCaptor.forClass(BulletProperties.class);
        verify(bulletLauncherFactory).create(bulletPropertiesCaptor.capture(), eq(audioEmitter), eq(collisionDetector));
        
        BulletProperties bulletProperties = bulletPropertiesCaptor.getValue();
        assertThat(bulletProperties.shotSounds()).isEmpty();
        assertThat(bulletProperties.trajectoryParticleEffect().particle()).isEqualTo(Particle.FLAME);
        assertThat(bulletProperties.trajectoryParticleEffect().count()).isEqualTo(1);
        assertThat(bulletProperties.trajectoryParticleEffect().offsetX()).isEqualTo(0.1);
        assertThat(bulletProperties.trajectoryParticleEffect().offsetY()).isEqualTo(0.2);
        assertThat(bulletProperties.trajectoryParticleEffect().offsetZ()).isEqualTo(0.3);
        assertThat(bulletProperties.trajectoryParticleEffect().extra()).isEqualTo(0.0);

        assertThat(projectileLauncher).isEqualTo(bulletLauncher);
    }

    private ProjectileSpec createBulletProjectileSpec() {
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec();
        trajectoryParticleEffectSpec.particle = "FLAME";
        trajectoryParticleEffectSpec.count = 1;
        trajectoryParticleEffectSpec.offsetX = 0.1;
        trajectoryParticleEffectSpec.offsetY = 0.2;
        trajectoryParticleEffectSpec.offsetZ = 0.3;
        trajectoryParticleEffectSpec.extra = 0.0;

        ProjectileSpec projectileSpec = new ProjectileSpec();
        projectileSpec.type = "BULLET";
        projectileSpec.trajectoryParticleEffect = trajectoryParticleEffectSpec;
        return projectileSpec;
    }
}
