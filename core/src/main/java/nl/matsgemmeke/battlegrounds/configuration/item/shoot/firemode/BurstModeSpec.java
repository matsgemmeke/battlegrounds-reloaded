package nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class BurstModeSpec extends FireModeSpec {

    @Required
    public Integer amountOfShots;

    @Required
    public Integer rateOfFire;

    @Required
    public Long cycleCooldown;
}
