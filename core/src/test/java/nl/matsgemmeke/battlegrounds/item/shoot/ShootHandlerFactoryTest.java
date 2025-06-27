package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.item.shoot.*;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilFactory;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ShootHandlerFactoryTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();

    private AmmunitionStorage ammunitionStorage;
    private FireModeFactory fireModeFactory;
    private ProjectileLauncherFactory projectileLauncherFactory;
    private RecoilFactory recoilFactory;
    private SpreadPatternFactory spreadPatternFactory;

    @BeforeEach
    public void setUp() {
        ammunitionStorage = new AmmunitionStorage(10, 20, 10, 20);
        fireModeFactory = mock(FireModeFactory.class);
        projectileLauncherFactory = mock(ProjectileLauncherFactory.class);
        recoilFactory = mock(RecoilFactory.class);
        spreadPatternFactory = mock(SpreadPatternFactory.class);
    }

    @Test
    public void createReturnsNewShootHandlerInstance() {
        FireMode fireMode = mock(FireMode.class);
        ItemRepresentation itemRepresentation = mock(ItemRepresentation.class);
        ProjectileLauncher projectileLauncher = mock(ProjectileLauncher.class);
        SpreadPattern spreadPattern = mock(SpreadPattern.class);

        FireModeSpec fireModeSpec = new FireModeSpec("FULLY_AUTOMATIC", null, 600, null);
        ProjectileSpec projectileSpec = new ProjectileSpec("BULLET", null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("SINGLE_PROJECTILE", null, null, null);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, null, spreadPatternSpec, null);

        when(fireModeFactory.create(fireModeSpec)).thenReturn(fireMode);
        when(projectileLauncherFactory.create(projectileSpec, GAME_KEY)).thenReturn(projectileLauncher);
        when(spreadPatternFactory.create(spreadPatternSpec)).thenReturn(spreadPattern);

        ShootHandlerFactory shootHandlerFactory = new ShootHandlerFactory(fireModeFactory, projectileLauncherFactory, recoilFactory, spreadPatternFactory);
        shootHandlerFactory.create(shootingSpec, GAME_KEY, ammunitionStorage, itemRepresentation);

        verify(fireMode).addShotObserver(any(ShotObserver.class));
    }

    @Test
    public void createReturnsNewShootHandlerInstanceWithRecoil() {
        FireMode fireMode = mock(FireMode.class);
        ItemRepresentation itemRepresentation = mock(ItemRepresentation.class);
        ProjectileLauncher projectileLauncher = mock(ProjectileLauncher.class);
        Recoil recoil = mock(Recoil.class);
        SpreadPattern spreadPattern = mock(SpreadPattern.class);

        FireModeSpec fireModeSpec = new FireModeSpec("FULLY_AUTOMATIC", null, 600, null);
        ProjectileSpec projectileSpec = new ProjectileSpec("BULLET", null, null);
        RecoilSpec recoilSpec = new RecoilSpec("RANDOM_SPREAD", List.of(0.1f), List.of(0.2f), null, null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("SINGLE_PROJECTILE", null, null, null);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, recoilSpec, spreadPatternSpec, null);

        when(fireModeFactory.create(fireModeSpec)).thenReturn(fireMode);
        when(projectileLauncherFactory.create(projectileSpec, GAME_KEY)).thenReturn(projectileLauncher);
        when(recoilFactory.create(recoilSpec)).thenReturn(recoil);
        when(spreadPatternFactory.create(spreadPatternSpec)).thenReturn(spreadPattern);

        ShootHandlerFactory shootHandlerFactory = new ShootHandlerFactory(fireModeFactory, projectileLauncherFactory, recoilFactory, spreadPatternFactory);
        shootHandlerFactory.create(shootingSpec, GAME_KEY, ammunitionStorage, itemRepresentation);

        verify(fireMode).addShotObserver(any(ShotObserver.class));
    }
}
