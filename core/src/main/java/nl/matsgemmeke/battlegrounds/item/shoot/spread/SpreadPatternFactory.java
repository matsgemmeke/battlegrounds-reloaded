package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.SpreadPatternSpecification;
import org.jetbrains.annotations.NotNull;

public class SpreadPatternFactory {

    @NotNull
    public SpreadPattern create(@NotNull SpreadPatternSpecification specification) {
        SpreadPatternType spreadPatternType = SpreadPatternType.valueOf(specification.type());

        int projectileAmount = specification.projectileAmount();
        float horizontalSpread = specification.horizontalSpread();
        float verticalSpread = specification.verticalSpread();

        if (spreadPatternType == SpreadPatternType.BUCKSHOT) {
            return new BuckshotSpreadPattern(projectileAmount, horizontalSpread, verticalSpread);
        }

        throw new IllegalArgumentException("Invalid spread pattern type \"" + specification.type() + "\"");
    }
}
