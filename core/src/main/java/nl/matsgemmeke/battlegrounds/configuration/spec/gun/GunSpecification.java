package nl.matsgemmeke.battlegrounds.configuration.spec.gun;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
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
        @NotNull ReloadSpec reloadSpec,
        @NotNull ItemStackSpec itemSpec,
        @NotNull ControlsSpecification controls,
        @NotNull FireModeSpec fireModeSpec,
        @Nullable RecoilSpec recoilSpec,
        @Nullable SpreadPatternSpec spreadPatternSpec
) { }
