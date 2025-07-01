package nl.matsgemmeke.battlegrounds.item.shoot;

import jakarta.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.RecoilSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ShootingSpec;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilFactory;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import org.jetbrains.annotations.NotNull;

public class ShootHandlerFactory {

    @NotNull
    private final FireModeFactory fireModeFactory;
    @NotNull
    private final ProjectileLauncherFactory projectileLauncherFactory;
    @NotNull
    private final RecoilFactory recoilFactory;
    @NotNull
    private final SpreadPatternFactory spreadPatternFactory;

    @Inject
    public ShootHandlerFactory(
            @NotNull FireModeFactory fireModeFactory,
            @NotNull ProjectileLauncherFactory projectileLauncherFactory,
            @NotNull RecoilFactory recoilFactory,
            @NotNull SpreadPatternFactory spreadPatternFactory
    ) {
        this.fireModeFactory = fireModeFactory;
        this.projectileLauncherFactory = projectileLauncherFactory;
        this.recoilFactory = recoilFactory;
        this.spreadPatternFactory = spreadPatternFactory;
    }

    @NotNull
    public ShootHandler create(@NotNull ShootingSpec spec, @NotNull GameKey gameKey, @NotNull AmmunitionStorage ammunitionStorage, @NotNull ItemRepresentation itemRepresentation) {
        FireMode fireMode = fireModeFactory.create(spec.fireMode);
        ProjectileLauncher projectileLauncher = projectileLauncherFactory.create(spec.projectile, gameKey);
        SpreadPattern spreadPattern = spreadPatternFactory.create(spec.spreadPattern);

        Recoil recoil = null;
        RecoilSpec recoilSpec = spec.recoil;

        if (recoilSpec != null) {
            recoil = recoilFactory.create(recoilSpec);
        }

        ShootHandler shootHandler = new ShootHandler(fireMode, projectileLauncher, spreadPattern, ammunitionStorage, itemRepresentation, recoil);
        shootHandler.registerObservers();
        return shootHandler;
    }
}
