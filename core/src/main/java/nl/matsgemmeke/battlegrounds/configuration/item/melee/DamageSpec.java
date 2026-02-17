package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class DamageSpec {

    @Required
    public Double meleeDamage;

    public Double throwingDamage;
}
