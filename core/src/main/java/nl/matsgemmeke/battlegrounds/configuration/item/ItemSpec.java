package nl.matsgemmeke.battlegrounds.configuration.item;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;
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

    public List<@EnumValue(type = ItemFlag.class) String> itemFlags = new ArrayList<>();

    public Map<String, @Valid DataSpec> data = new HashMap<>();

    public Boolean unbreakable = false;
}
