package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class RangeProfileSpec {

    @Required
    public DamageDistanceSpec shortRange;

    @Required
    public DamageDistanceSpec mediumRange;

    @Required
    public DamageDistanceSpec longRange;
}
