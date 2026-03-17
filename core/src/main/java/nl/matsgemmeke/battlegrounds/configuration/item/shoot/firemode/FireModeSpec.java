package nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public abstract class FireModeSpec {

    @Required
    @EnumValue(type = FireModeType.class)
    public String type;
}
