package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DamageSpec {

    @Required
    public Double damageAmount;

    @Required
    public Boolean throwable;
}
