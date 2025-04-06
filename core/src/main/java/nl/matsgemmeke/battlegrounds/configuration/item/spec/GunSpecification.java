package nl.matsgemmeke.battlegrounds.configuration.item.spec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GunSpecification(
        @NotNull String name,
        @Nullable String description,
        @NotNull Integer magazineSize,
        @NotNull Integer maxMagazineAmount,
        @NotNull Integer defaultMagazineAmount,
        @NotNull Double shortRangeDamage,
        @NotNull Double shortRangeDistance,
        @NotNull Double mediumRangeDamage,
        @NotNull Double mediumRangeDistance,
        @NotNull Double longRangeDamage,
        @NotNull Double longRangeDistance,
        @NotNull Double headshotDamageMultiplier,
        @Nullable String shotSounds,
        @NotNull FireModeSpecification fireMode
) { }
