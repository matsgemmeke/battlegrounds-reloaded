package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class AmmoSpec {

    @Required
    public Integer defaultAmount;

    @Required
    public Integer maxAmount;
}
