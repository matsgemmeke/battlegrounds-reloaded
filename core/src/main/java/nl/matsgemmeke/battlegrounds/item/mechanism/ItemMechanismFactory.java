package nl.matsgemmeke.battlegrounds.item.mechanism;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
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
                CollisionDetector collisionDetector = context.getCollisionDetector();

                float power = section.getFloat("power");
                boolean setFire = section.getBoolean("set-fire");
                boolean breakBlocks = section.getBoolean("break-blocks");

                double longRangeDamage = section.getDouble("range.long-range.damage");
                double longRangeDistance = section.getDouble("range.long-range.distance");
                double mediumRangeDamage = section.getDouble("range.medium-range.damage");
                double mediumRangeDistance = section.getDouble("range.medium-range.distance");
                double shortRangeDamage = section.getDouble("range.short-range.damage");
                double shortRangeDistance = section.getDouble("range.short-range.distance");

                RangeProfile rangeProfile = new RangeProfile(longRangeDamage, longRangeDistance, mediumRangeDamage, mediumRangeDistance, shortRangeDamage, shortRangeDistance);

                return new ExplosionMechanism(collisionDetector, rangeProfile, power, setFire, breakBlocks);
            }
        }

        throw new InvalidItemConfigurationException("Unknown equipment mechanism type \"" + type + "\"!");
    }
}
