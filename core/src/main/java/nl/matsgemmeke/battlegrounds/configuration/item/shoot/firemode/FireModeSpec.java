package nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public abstract class FireModeSpec {

    @Required
    @EnumValue(type = FireModeType.class)
    public String type;
}
