package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class SpreadPatternSpec {

    @Required
    @EnumValue(SpreadPatternType.class)
    public String type;
    public Integer projectileAmount;
    public Float horizontalSpread;
    public Float verticalSpread;

    private enum SpreadPatternType {
        SINGLE_PROJECTILE, BUCKSHOT
    }
}
