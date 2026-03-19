package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.item.type.Action;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class ControlsSpec {

    @Required
    @EnumValue(type = Action.class)
    public String reload;

    @Required
    @EnumValue(type = Action.class)
    public String shoot;

    @EnumValue(type = Action.class)
    public String scopeUse;

    @EnumValue(type = Action.class)
    public String scopeStop;

    @EnumValue(type = Action.class)
    public String scopeChangeMagnification;
}
