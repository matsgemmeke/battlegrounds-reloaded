package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.EnumValues;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemSpec {

    @Required
    @EnumValue(type = Material.class)
    public String material;

    public String displayName;

    public Integer damage = 0;

    @EnumValues(type = ItemFlag.class)
    public List<String> itemFlags = new ArrayList<>();

    public Map<String, DataSpec> data = new HashMap<>();

    public Boolean unbreakable = false;
}
