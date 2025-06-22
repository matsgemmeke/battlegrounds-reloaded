package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ShootingSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
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

public class ShootHandlerFactoryTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();

    private BulletLauncherFactory bulletLauncherFactory;
    private FireModeFactory fireModeFactory;
    private GameContextProvider contextProvider;
    private ParticleEffectMapper particleEffectMapper;

    @BeforeEach
    public void setUp() {
        bulletLauncherFactory = mock(BulletLauncherFactory.class);
        fireModeFactory = mock(FireModeFactory.class);
        contextProvider = mock(GameContextProvider.class);
        particleEffectMapper = new ParticleEffectMapper();
    }

    @Test
    public void createReturnsNewShootHandlerInstanceWithTrajectoryParticleEffect() {
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        FireMode fireMode = mock(FireMode.class);
        ItemRepresentation itemRepresentation = mock(ItemRepresentation.class);
        ProjectileLauncher projectileLauncher = mock(ProjectileLauncher.class);

        FireModeSpec fireModeSpec = new FireModeSpec("FULLY_AUTOMATIC", null, 600, null);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.1, 0.2, 0.3, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, null);

        when(bulletLauncherFactory.create(any(BulletProperties.class), eq(collisionDetector))).thenReturn(projectileLauncher);
        when(contextProvider.getComponent(GAME_KEY, CollisionDetector.class)).thenReturn(collisionDetector);
        when(fireModeFactory.create(fireModeSpec)).thenReturn(fireMode);

        ShootHandlerFactory shootHandlerFactory = new ShootHandlerFactory(bulletLauncherFactory, fireModeFactory, contextProvider, particleEffectMapper);
        shootHandlerFactory.create(shootingSpec, GAME_KEY, itemRepresentation);

        ArgumentCaptor<BulletProperties> bulletPropertiesCaptor = ArgumentCaptor.forClass(BulletProperties.class);
        verify(bulletLauncherFactory).create(bulletPropertiesCaptor.capture(), eq(collisionDetector));

        ParticleEffect trajectoryParticleEffect = bulletPropertiesCaptor.getValue().trajectoryParticleEffect();
        assertThat(trajectoryParticleEffect.particle()).isEqualTo(Particle.FLAME);
        assertThat(trajectoryParticleEffect.count()).isEqualTo(1);
        assertThat(trajectoryParticleEffect.offsetX()).isEqualTo(0.1);
        assertThat(trajectoryParticleEffect.offsetY()).isEqualTo(0.2);
        assertThat(trajectoryParticleEffect.offsetZ()).isEqualTo(0.3);
        assertThat(trajectoryParticleEffect.extra()).isEqualTo(0.0);
        assertThat(trajectoryParticleEffect.blockDataMaterial()).isNull();
        assertThat(trajectoryParticleEffect.dustOptions()).isNull();

        verify(fireMode).addShotObserver(any(ShotObserver.class));
    }
}
