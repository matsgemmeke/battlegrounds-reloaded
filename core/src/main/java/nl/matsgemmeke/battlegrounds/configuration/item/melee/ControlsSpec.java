package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.type.ActionConfigurationValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;

public class ControlsSpec {

    @EnumValue(type = ActionConfigurationValue.class)
    public String throwing;
}
