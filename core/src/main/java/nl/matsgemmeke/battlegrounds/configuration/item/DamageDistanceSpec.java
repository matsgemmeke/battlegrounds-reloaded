package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DamageDistanceSpec {

    @Required
    public Double damage;
    @Required
    public Double distance;
}
