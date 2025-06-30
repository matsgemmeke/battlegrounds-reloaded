package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ItemSpec {

    @Required
    public String material;
    @Required
    public String displayName;
    @Required
    public Integer damage;
}
