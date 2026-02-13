package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class AmmoSpec {

    @Required
    public Integer loadedAmount;

    @Required
    public Integer maxLoadedAmount;

    @Required
    public Integer defaultReserveAmount;

    @Required
    public Integer maxReserveAmount;
}
