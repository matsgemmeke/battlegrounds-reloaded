package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.ConditionalRequired;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class SpreadPatternSpec {

    @Required
    @EnumValue(type = SpreadPatternType.class)
    public String type;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "BUCKSHOT")
    public Integer projectileAmount;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "BUCKSHOT")
    public Float horizontalSpread;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "BUCKSHOT")
    public Float verticalSpread;

    private enum SpreadPatternType {
        SINGLE_PROJECTILE, BUCKSHOT
    }
}
