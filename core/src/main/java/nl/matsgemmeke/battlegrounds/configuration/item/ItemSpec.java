package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.EnumValues;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class ItemSpec {

    @Required
    @EnumValue(type = Material.class)
    public String material;

    public String displayName;

    @Required
    public Integer damage;

    @EnumValues(type = ItemFlag.class)
    public List<String> itemFlags = new ArrayList<>();

    public Boolean unbreakable = false;
}
