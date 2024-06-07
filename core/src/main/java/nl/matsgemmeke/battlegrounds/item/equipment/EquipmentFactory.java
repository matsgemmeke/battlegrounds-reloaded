package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.mechanism.TimedExplosionMechanism;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        return equipment;
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

        // Read controls configuration
        Section controlsSection = section.getSection("controls");

        if (controlsSection != null) {
            this.addControls(equipment, context, section, controlsSection);
        }

        return equipment;
    }

    private void addControls(@NotNull DefaultEquipment equipment, @NotNull GameContext context, @NotNull Section section, @NotNull Section controlsSection) {
        String throwActionValue = controlsSection.getString("throw");

        if (throwActionValue != null) {
            double projectileSpeed = section.getDouble("throwing.projectile-speed");

            ThrowFunction throwFunction = new ThrowFunction(new TimedExplosionMechanism(), equipment.getItemStack(), context, projectileSpeed);

            List<GameSound> shotSounds = DefaultGameSound.parseSounds(section.getString("throwing.sound"));
            throwFunction.addSounds(shotSounds);

            Action throwAction = this.getActionFromConfiguration("throw", throwActionValue);

            equipment.getControls().addControl(throwAction, throwFunction);
        }
    }

    @NotNull
    private Action getActionFromConfiguration(@NotNull String functionName, @NotNull String value) {
        try {
            return Action.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new CreateEquipmentException("Error while getting controls for " + functionName + ": \""
                    + value + "\" is not a valid action type!");
        }
    }
}
