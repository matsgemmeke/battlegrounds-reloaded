package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;
import org.bukkit.Material;

public class ItemSpec {

    @Required
    @EnumValue(Material.class)
    public String material;
    @Required
    public String displayName;
    @Required
    public Integer damage;
}
