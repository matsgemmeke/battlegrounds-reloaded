package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;

public class SpreadPatternFactory {

    @NotNull
    public SpreadPattern make(@NotNull Section section) {
        String type = section.getString("type");
        SpreadPatternType spreadPatternType;

        try {
            spreadPatternType = SpreadPatternType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error while getting spread pattern type \"" + type + "\"");
        }

        int pelletAmount = section.getInt("pellet-amount");
        float horizontalSpread = section.getFloat("horizontal-spread");
        float verticalSpread = section.getFloat("vertical-spread");

        if (spreadPatternType == SpreadPatternType.BUCKSHOT) {
            return new BuckshotSpreadPattern(pelletAmount, horizontalSpread, verticalSpread);
        }

        throw new IllegalArgumentException("Invalid spread pattern type \"" + type + "\"");
    }
}
