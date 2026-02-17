package nl.matsgemmeke.battlegrounds.item.shoot;

import jakarta.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.RecoilSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ShootingSpec;
import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;

public class ShootHandlerFactory {

    private final FireModeFactory fireModeFactory;
    private final ProjectileLauncherFactory projectileLauncherFactory;
    private final RecoilFactory recoilFactory;
    private final SpreadPatternFactory spreadPatternFactory;

    @Inject
    public ShootHandlerFactory(FireModeFactory fireModeFactory, ProjectileLauncherFactory projectileLauncherFactory, RecoilFactory recoilFactory, SpreadPatternFactory spreadPatternFactory) {
        this.fireModeFactory = fireModeFactory;
        this.projectileLauncherFactory = projectileLauncherFactory;
        this.recoilFactory = recoilFactory;
        this.spreadPatternFactory = spreadPatternFactory;
    }

    public ShootHandler create(ShootingSpec spec, ResourceContainer resourceContainer, ItemRepresentation itemRepresentation) {
        FireMode fireMode = fireModeFactory.create(spec.fireMode);
        ProjectileLauncher projectileLauncher = projectileLauncherFactory.create(spec.projectile);
        SpreadPattern spreadPattern = spreadPatternFactory.create(spec.spreadPattern);

        Recoil recoil = null;
        RecoilSpec recoilSpec = spec.recoil;

        if (recoilSpec != null) {
            recoil = recoilFactory.create(recoilSpec);
        }

        ShootHandler shootHandler = new ShootHandler(fireMode, projectileLauncher, spreadPattern, resourceContainer, itemRepresentation, recoil);
        shootHandler.registerObservers();
        return shootHandler;
    }
}
