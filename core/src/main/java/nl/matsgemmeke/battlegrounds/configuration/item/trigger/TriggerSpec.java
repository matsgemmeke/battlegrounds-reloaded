package nl.matsgemmeke.battlegrounds.configuration.item.trigger;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public abstract class TriggerSpec {

    @Required
    @EnumValue(type = TriggerType.class)
    public String type;

    public Boolean repeating = false;
}
