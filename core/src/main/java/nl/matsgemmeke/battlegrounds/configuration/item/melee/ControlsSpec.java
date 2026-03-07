package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.type.Action;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;

public class ControlsSpec {

    @EnumValue(type = Action.class)
    public String reload;

    @EnumValue(type = Action.class)
    public String throwing;
}
