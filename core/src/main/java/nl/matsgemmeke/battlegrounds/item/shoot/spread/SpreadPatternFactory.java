package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread.BuckshotSpreadPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.spread.SpreadPatternSpec;

public class SpreadPatternFactory {

    public SpreadPattern create(SpreadPatternSpec spreadPatternSpec) {
        SpreadPatternType spreadPatternType = SpreadPatternType.valueOf(spreadPatternSpec.type);

        return switch (spreadPatternType) {
            case BUCKSHOT -> {
                BuckshotSpreadPatternSpec spec = (BuckshotSpreadPatternSpec) spreadPatternSpec;

                yield new BuckshotSpreadPattern(spec.projectileAmount, spec.horizontalSpread, spec.verticalSpread);
            }
            case SINGLE_PROJECTILE -> new SingleProjectileSpreadPattern();
        };
    }
}
