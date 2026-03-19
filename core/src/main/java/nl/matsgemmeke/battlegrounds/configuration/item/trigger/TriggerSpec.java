package nl.matsgemmeke.battlegrounds.configuration.item.trigger;

import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public abstract class TriggerSpec {

    @Required
    @EnumValue(type = TriggerType.class)
    public String type;

    public Boolean repeating = false;
}
