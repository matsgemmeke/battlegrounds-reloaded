package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class SpreadPatternSpec {

    @Required
    public String type;
    public Integer projectileAmount;
    public Float horizontalSpread;
    public Float verticalSpread;
}
