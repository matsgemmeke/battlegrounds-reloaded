package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.type.Action;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;

public class ControlsSpec {

    @EnumValue(type = Action.class)
    public String throwing;

    @EnumValue(type = Action.class)
    public String cook;

    @EnumValue(type = Action.class)
    public String drop;

    @EnumValue(type = Action.class)
    public String place;

    @EnumValue(type = Action.class)
    public String activate;
}
