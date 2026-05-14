package nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public abstract class SpreadPatternSpec {

    @Required
    @EnumValue(type = SpreadPatternType.class)
    public String type;
}
