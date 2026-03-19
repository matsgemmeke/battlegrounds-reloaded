package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;
import org.bukkit.Material;

public class PlacePropertiesSpec {

    @Required
    @EnumValue(type = Material.class)
    public String material;

    @Required
    public Long cooldown;

    public String placeSounds;
}
