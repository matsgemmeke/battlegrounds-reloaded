package nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class FullyAutomaticModeSpec extends FireModeSpec {

    @Required
    public Integer rateOfFire;
}
