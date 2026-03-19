package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class DamageDistanceSpec {

    @Required
    public Double damage;

    @Required
    public Double distance;
}
