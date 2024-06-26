package nl.matsgemmeke.battlegrounds.item.mechanism;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import org.jetbrains.annotations.NotNull;

public class ItemMechanismFactory {

    public ItemMechanism make(@NotNull Section section, @NotNull GameContext context) {
        String type = section.getString("type");

        if (type == null) {
            throw new InvalidItemConfigurationException("Equipment mechanism type must be defined!");
        }

        ItemMechanismType equipmentMechanismType;

        try {
            equipmentMechanismType = ItemMechanismType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidItemConfigurationException("Equipment mechanism type \"" + type + "\" is invalid!");
        }

        switch (equipmentMechanismType) {
            case EXPLOSION -> {
                return new ExplosionMechanism(context);
            }
        }

        throw new InvalidItemConfigurationException("Unknown equipment mechanism type \"" + type + "\"!");
    }
}
