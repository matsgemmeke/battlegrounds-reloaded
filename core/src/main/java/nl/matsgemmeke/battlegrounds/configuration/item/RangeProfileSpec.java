package nl.matsgemmeke.battlegrounds.configuration.item;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class RangeProfileSpec {

    @Required
    @Valid
    public DamageDistanceSpec shortRange;

    @Required
    @Valid
    public DamageDistanceSpec mediumRange;

    @Required
    @Valid
    public DamageDistanceSpec longRange;
}
