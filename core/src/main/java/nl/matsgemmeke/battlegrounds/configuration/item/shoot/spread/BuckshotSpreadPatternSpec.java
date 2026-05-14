package nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class BuckshotSpreadPatternSpec extends SpreadPatternSpec {

    @Required
    public Integer projectileAmount;

    @Required
    public Float horizontalSpread;

    @Required
    public Float verticalSpread;
}
