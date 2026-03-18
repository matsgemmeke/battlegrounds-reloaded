package nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public abstract class SpreadPatternSpec {

    @Required
    @EnumValue(type = SpreadPatternType.class)
    public String type;
}
