package nl.matsgemmeke.battlegrounds.configuration.spec.gun;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RecoilSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.SpreadPatternSpecification;
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
        @NotNull ItemStackSpecification item,
        @NotNull ControlsSpecification controls,
        @NotNull FireModeSpecification fireMode,
        @Nullable RecoilSpecification recoil,
        @Nullable SpreadPatternSpecification spreadPattern
) { }
