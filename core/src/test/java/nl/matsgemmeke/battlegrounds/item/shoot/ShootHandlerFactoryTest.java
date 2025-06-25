package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.*;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.mapper.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ShootHandlerFactoryTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();

    private AmmunitionStorage ammunitionStorage;
    private BulletLauncherFactory bulletLauncherFactory;
    private FireModeFactory fireModeFactory;
    private GameContextProvider contextProvider;
    private ParticleEffectMapper particleEffectMapper;
    private SpreadPatternFactory spreadPatternFactory;

    @BeforeEach
    public void setUp() {
        ammunitionStorage = new AmmunitionStorage(10, 20, 10, 20);
        bulletLauncherFactory = mock(BulletLauncherFactory.class);
        fireModeFactory = mock(FireModeFactory.class);
        contextProvider = mock(GameContextProvider.class);
        particleEffectMapper = new ParticleEffectMapper();
        spreadPatternFactory = mock(SpreadPatternFactory.class);
    }

    @Test
    public void createReturnsNewShootHandlerInstanceWithTrajectoryParticleEffect() {
        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        FireMode fireMode = mock(FireMode.class);
        ItemRepresentation itemRepresentation = mock(ItemRepresentation.class);
        ProjectileLauncher projectileLauncher = mock(ProjectileLauncher.class);
        SpreadPattern spreadPattern = mock(SpreadPattern.class);

        FireModeSpec fireModeSpec = new FireModeSpec("FULLY_AUTOMATIC", null, 600, null);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.1, 0.2, 0.3, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        RecoilSpec recoilSpec = new RecoilSpec("RANDOM_SPREAD", List.of(0.1f), List.of(0.2f), null, null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("SINGLE_PROJECTILE", null, null, null);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, recoilSpec, spreadPatternSpec, null);

        when(bulletLauncherFactory.create(any(BulletProperties.class), eq(audioEmitter), eq(collisionDetector))).thenReturn(projectileLauncher);
        when(contextProvider.getComponent(GAME_KEY, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(GAME_KEY, CollisionDetector.class)).thenReturn(collisionDetector);
        when(fireModeFactory.create(fireModeSpec)).thenReturn(fireMode);
        when(spreadPatternFactory.create(spreadPatternSpec)).thenReturn(spreadPattern);

        ShootHandlerFactory shootHandlerFactory = new ShootHandlerFactory(bulletLauncherFactory, fireModeFactory, contextProvider, particleEffectMapper, spreadPatternFactory);
        shootHandlerFactory.create(shootingSpec, GAME_KEY, ammunitionStorage, itemRepresentation);

        ArgumentCaptor<BulletProperties> bulletPropertiesCaptor = ArgumentCaptor.forClass(BulletProperties.class);
        verify(bulletLauncherFactory).create(bulletPropertiesCaptor.capture(), eq(audioEmitter), eq(collisionDetector));

        BulletProperties properties = bulletPropertiesCaptor.getValue();
        assertThat(properties.trajectoryParticleEffect().particle()).isEqualTo(Particle.FLAME);
        assertThat(properties.trajectoryParticleEffect().count()).isEqualTo(1);
        assertThat(properties.trajectoryParticleEffect().offsetX()).isEqualTo(0.1);
        assertThat(properties.trajectoryParticleEffect().offsetY()).isEqualTo(0.2);
        assertThat(properties.trajectoryParticleEffect().offsetZ()).isEqualTo(0.3);
        assertThat(properties.trajectoryParticleEffect().extra()).isEqualTo(0.0);
        assertThat(properties.trajectoryParticleEffect().blockDataMaterial()).isNull();
        assertThat(properties.trajectoryParticleEffect().dustOptions()).isNull();
        assertThat(properties.shotSounds()).isEmpty();

        verify(fireMode).addShotObserver(any(ShotObserver.class));
    }
}
