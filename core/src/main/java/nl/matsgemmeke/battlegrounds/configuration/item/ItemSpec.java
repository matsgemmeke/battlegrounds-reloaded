package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;
import org.bukkit.Material;

public class ItemSpec {

    @Required
    @EnumValue(type = Material.class)
    public String material;
    public String displayName;
    @Required
    public Integer damage;
}
