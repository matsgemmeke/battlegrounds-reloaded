package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import org.bukkit.Material;

public class PlacePropertiesSpec {

    @NotNull
    @EnumValue(type = Material.class)
    public String material;

    @NotNull
    public Long cooldown;

    public String soundTemplate;
}
