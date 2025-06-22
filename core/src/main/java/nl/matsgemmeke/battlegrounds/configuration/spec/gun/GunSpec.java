package nl.matsgemmeke.battlegrounds.configuration.spec.gun;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for a gun loaded from a YAML file.
 * <p>
 * This record is constructed after the corresponding YAML file has been read and all values have been parsed and
 * validated. Instances of this record hold all the necessary configuration values which are guaranteed to be valid at
 * the time of instantiation.
 *
 * @param id                       the gun id
 * @param name                     the display name of the gun
 * @param description              the description of the gun
 * @param magazineSize             the size of the gun's magazine
 * @param maxMagazineAmount        the maximum number of magazines the holder can carry for the gun
 * @param defaultMagazineAmount    the default number of magazines the holder will carry when receiving the gun
 * @param rangeProfile             the range profile specification
 * @param headshotDamageMultiplier the damage multiplier when hitting headshots
 * @param shooting                 the specification for shooting
 * @param reload                   the specification for the reload system
 * @param item                     the specification for the display item, held by the user
 * @param controls                 the specification for the gun's controls
 * @param recoil                   the specification for the recoil
 * @param scope                    the specification for the scope
 * @param spreadPattern            the specification for the spread pattern
 */
public record GunSpec(
        @NotNull String id,
        @NotNull String name,
        @Nullable String description,
        @NotNull Integer magazineSize,
        @NotNull Integer maxMagazineAmount,
        @NotNull Integer defaultMagazineAmount,
        @NotNull RangeProfileSpec rangeProfile,
        @NotNull Double headshotDamageMultiplier,
        @NotNull ShootingSpec shooting,
        @NotNull ReloadSpec reload,
        @NotNull ItemStackSpec item,
        @NotNull ControlsSpec controls,
        @Nullable RecoilSpec recoil,
        @Nullable ScopeSpec scope,
        @Nullable SpreadPatternSpec spreadPattern
) { }
