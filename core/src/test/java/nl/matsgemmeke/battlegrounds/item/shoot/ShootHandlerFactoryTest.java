package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.*;
import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.ShotObserver;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShootHandlerFactoryTest {

    private static final ResourceContainer RESOURCE_CONTAINER = new ResourceContainer(30, 30, 90, 300);

    @Mock
    private FireModeFactory fireModeFactory;
    @Mock
    private ProjectileLauncherFactory projectileLauncherFactory;
    @Mock
    private RecoilFactory recoilFactory;
    @Mock
    private SpreadPatternFactory spreadPatternFactory;
    @InjectMocks
    private ShootHandlerFactory shootHandlerFactory;

    @Test
    @DisplayName("create returns ShootHandler instance without recoil")
    void create_withoutRecoil() {
        FireMode fireMode = mock(FireMode.class);
        ItemRepresentation itemRepresentation = mock(ItemRepresentation.class);
        ProjectileLauncher projectileLauncher = mock(ProjectileLauncher.class);
        SpreadPattern spreadPattern = mock(SpreadPattern.class);
        ShootingSpec shootingSpec = this.createShootingSpec();

        when(fireModeFactory.create(shootingSpec.fireMode)).thenReturn(fireMode);
        when(projectileLauncherFactory.create(shootingSpec.projectile)).thenReturn(projectileLauncher);
        when(spreadPatternFactory.create(shootingSpec.spreadPattern)).thenReturn(spreadPattern);

        shootHandlerFactory.create(shootingSpec, RESOURCE_CONTAINER, itemRepresentation);

        verify(fireMode).addShotObserver(any(ShotObserver.class));
    }

    @Test
    @DisplayName("create returns ShootHandler instance with recoil")
    void create_withRecoil() {
        FireMode fireMode = mock(FireMode.class);
        ItemRepresentation itemRepresentation = mock(ItemRepresentation.class);
        ProjectileLauncher projectileLauncher = mock(ProjectileLauncher.class);
        Recoil recoil = mock(Recoil.class);
        SpreadPattern spreadPattern = mock(SpreadPattern.class);

        RecoilSpec recoilSpec = new RecoilSpec();
        recoilSpec.type = "RANDOM_SPREAD";
        recoilSpec.horizontal = new Float[] { 0.1f };
        recoilSpec.vertical = new Float[] { 0.2f };

        ShootingSpec shootingSpec = this.createShootingSpec();
        shootingSpec.recoil = recoilSpec;

        when(fireModeFactory.create(shootingSpec.fireMode)).thenReturn(fireMode);
        when(projectileLauncherFactory.create(shootingSpec.projectile)).thenReturn(projectileLauncher);
        when(recoilFactory.create(recoilSpec)).thenReturn(recoil);
        when(spreadPatternFactory.create(shootingSpec.spreadPattern)).thenReturn(spreadPattern);

        ShootHandlerFactory shootHandlerFactory = new ShootHandlerFactory(fireModeFactory, projectileLauncherFactory, recoilFactory, spreadPatternFactory);
        shootHandlerFactory.create(shootingSpec, RESOURCE_CONTAINER, itemRepresentation);

        verify(fireMode).addShotObserver(any(ShotObserver.class));
    }

    private ShootingSpec createShootingSpec() {
        FireModeSpec fireModeSpec = new FireModeSpec();
        fireModeSpec.type = "FULLY_AUTOMATIC";
        fireModeSpec.rateOfFire = 600;

        ProjectileSpec projectileSpec = new ProjectileSpec();
        projectileSpec.type = "BULLET";

        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec();
        spreadPatternSpec.type = "SINGLE_PROJECTILE";

        ShootingSpec shootingSpec = new ShootingSpec();
        shootingSpec.fireMode = fireModeSpec;
        shootingSpec.projectile = projectileSpec;
        shootingSpec.spreadPattern = spreadPatternSpec;
        return shootingSpec;
    }
}
