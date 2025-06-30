package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class FireModeSpec {

    @Required
    public String type;
    public Integer amountOfShots;
    public Integer rateOfFire;
    public Long cycleCooldown;
}
