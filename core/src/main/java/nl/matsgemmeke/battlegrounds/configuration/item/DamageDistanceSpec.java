package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class DamageDistanceSpec {

    @Required
    public Double damage;

    @Required
    public Double distance;
}
