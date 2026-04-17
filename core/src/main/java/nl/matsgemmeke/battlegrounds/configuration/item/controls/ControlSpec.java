package nl.matsgemmeke.battlegrounds.configuration.item.controls;

import jakarta.validation.constraints.Min;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class ControlSpec {

    @Required
    @EnumValue(type = Action.class)
    public String action;

    @Min(value = 1, message = "priority must be a value of 1 or higher")
    public int priority = 1;

    public boolean stopsChain = false;

    public boolean blocking = false;

    public boolean cancelsEvent = false;
}
