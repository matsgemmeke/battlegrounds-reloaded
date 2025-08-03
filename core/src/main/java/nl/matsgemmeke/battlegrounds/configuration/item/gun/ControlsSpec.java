package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.item.type.ActionConfigurationValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ControlsSpec {

    @Required
    @EnumValue(type = ActionConfigurationValue.class)
    public String reload;
    @Required
    @EnumValue(type = ActionConfigurationValue.class)
    public String shoot;
    @EnumValue(type = ActionConfigurationValue.class)
    public String scopeUse;
    @EnumValue(type = ActionConfigurationValue.class)
    public String scopeStop;
    @EnumValue(type = ActionConfigurationValue.class)
    public String scopeChangeMagnification;
}
