package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class EquipmentItemsSpec {

    @Required
    @Valid
    public ItemSpec displayItem;

    @Valid
    public ItemSpec activatorItem;

    @Valid
    public ItemSpec throwItem;
}
