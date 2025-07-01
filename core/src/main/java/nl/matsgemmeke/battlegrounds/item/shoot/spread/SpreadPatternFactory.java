package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.SpreadPatternSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpreadPatternFactory {

    @NotNull
    public SpreadPattern create(@NotNull SpreadPatternSpec spec) {
        SpreadPatternType spreadPatternType = SpreadPatternType.valueOf(spec.type);

        switch (spreadPatternType) {
            case BUCKSHOT -> {
                int projectileAmount = this.validateSpecVar(spec.projectileAmount, "projectileAmount", spreadPatternType);
                float horizontalSpread = this.validateSpecVar(spec.horizontalSpread, "horizontalSpread", spreadPatternType);
                float verticalSpread = this.validateSpecVar(spec.verticalSpread, "verticalSpread", spreadPatternType);

                return new BuckshotSpreadPattern(projectileAmount, horizontalSpread, verticalSpread);
            }
            case SINGLE_PROJECTILE -> {
                return new SingleProjectileSpreadPattern();
            }
            default -> throw new SpreadPatternCreationException("Invalid spread pattern type \"" + spec.type + "\"");
        }
    }

    private <T> T validateSpecVar(@Nullable T value, @NotNull String valueName, @NotNull Object spreadPatternType) {
        if (value == null) {
            throw new SpreadPatternCreationException("Cannot create spread pattern with type %s because of invalid spec: Required '%s' value is missing".formatted(spreadPatternType, valueName));
        }

        return value;
    }
}
