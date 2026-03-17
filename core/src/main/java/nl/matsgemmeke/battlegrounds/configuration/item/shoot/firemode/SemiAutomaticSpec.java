package nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class SemiAutomaticSpec extends FireModeSpec {

    @Required
    public Long cycleCooldown;
}
