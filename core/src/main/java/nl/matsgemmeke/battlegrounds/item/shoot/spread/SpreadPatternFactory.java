package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.SpreadPatternSpec;
import org.jetbrains.annotations.NotNull;

public class SpreadPatternFactory {

    @NotNull
    public SpreadPattern create(@NotNull SpreadPatternSpec spec) {
        SpreadPatternType spreadPatternType = SpreadPatternType.valueOf(spec.type());

        int projectileAmount = spec.projectileAmount();
        float horizontalSpread = spec.horizontalSpread();
        float verticalSpread = spec.verticalSpread();

        if (spreadPatternType == SpreadPatternType.BUCKSHOT) {
            return new BuckshotSpreadPattern(projectileAmount, horizontalSpread, verticalSpread);
        }

        throw new IllegalArgumentException("Invalid spread pattern type \"" + spec.type() + "\"");
    }
}
