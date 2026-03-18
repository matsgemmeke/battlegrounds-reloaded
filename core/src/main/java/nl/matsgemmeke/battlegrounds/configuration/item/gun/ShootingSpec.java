package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.recoil.RecoilSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.FireModeSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class ShootingSpec {

    @Required
    @Valid
    public FireModeSpec fireMode;

    @Required
    @Valid
    public ProjectileSpec projectile;

    @Valid
    public RecoilSpec recoil;

    @Required
    @Valid
    public SpreadPatternSpec spreadPattern;
}
