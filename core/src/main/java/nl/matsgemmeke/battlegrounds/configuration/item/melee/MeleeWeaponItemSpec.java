package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class MeleeWeaponItemSpec {

    @Required
    public ItemSpec displayItem;

    public ItemSpec throwItem;
}
