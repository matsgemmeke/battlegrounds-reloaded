package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class AmmoSpec {

    @Required
    public Integer magazineSize;
    @Required
    public Integer defaultMagazineAmount;
    @Required
    public Integer maxMagazineAmount;
}
