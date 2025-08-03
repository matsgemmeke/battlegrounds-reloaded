package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ShootingSpec {

    @Required
    public FireModeSpec fireMode;
    @Required
    public ProjectileSpec projectile;
    public RecoilSpec recoil;
    @Required
    public SpreadPatternSpec spreadPattern;
}
