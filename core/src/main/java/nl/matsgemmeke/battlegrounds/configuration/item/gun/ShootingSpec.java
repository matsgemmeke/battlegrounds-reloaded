package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ShootingSpec {

    @Required
    public FireModeSpec fireMode;
    @Required
    public ProjectileSpec projectile;
    @Required
    public RangeProfileSpec range;
    public RecoilSpec recoil;
    @Required
    public SpreadPatternSpec spreadPattern;
}
