package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentFactory implements WeaponFactory {

    @NotNull
    public Equipment make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context) {
        Equipment equipment = this.createInstance(configuration, context);

        game.getEquipmentRegister().addUnassignedItem(equipment);

        return equipment;
    }

    @NotNull
    public Equipment make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context, @NotNull GamePlayer gamePlayer) {
        Equipment equipment = this.createInstance(configuration, context);
        equipment.setHolder(gamePlayer);

        game.getEquipmentRegister().addAssignedItem(equipment, gamePlayer);

        return this.createInstance(configuration, context);
    }

    @NotNull
    private Equipment createInstance(@NotNull ItemConfiguration configuration, @NotNull GameContext context) {
        Section section = configuration.getRoot();

        String name = section.getString("display-name");
        String description = section.getString("description");

        DefaultEquipment equipment = new DefaultEquipment(context);
        equipment.setName(name);
        equipment.setDescription(description);

        // ItemStack creation
        Material material = Material.getMaterial(section.getString("item.material"));
        short durability = section.getShort("item.durability");

        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability(durability);

        equipment.setItemStack(itemStack);

        return equipment;
    }
}
