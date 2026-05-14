package nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class SemiAutomaticSpec extends FireModeSpec {

    @Required
    public Long cycleCooldown;
}
