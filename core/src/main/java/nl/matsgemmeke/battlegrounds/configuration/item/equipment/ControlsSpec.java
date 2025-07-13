package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.type.ActionConfigurationValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;

public class ControlsSpec {

    @EnumValue(type = ActionConfigurationValue.class)
    public String throwing;
    @EnumValue(type = ActionConfigurationValue.class)
    public String cook;
    @EnumValue(type = ActionConfigurationValue.class)
    public String place;
    @EnumValue(type = ActionConfigurationValue.class)
    public String activate;
}
