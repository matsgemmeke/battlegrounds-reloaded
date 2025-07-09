package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;
import org.bukkit.Material;

public class PlacePropertiesSpec {

    @Required
    @EnumValue(type = Material.class)
    public String material;
    @Required
    public Long cooldown;
    public String placeSounds;
}
