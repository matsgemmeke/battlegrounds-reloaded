package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class EquipmentItemsSpec {

    @Required
    public ItemSpec displayItem;
    public ItemSpec activatorItem;
    public ItemSpec throwItem;
}
