package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import nl.matsgemmeke.battlegrounds.configuration.item.shoot.SpreadPatternSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ShootingSpec(
        @NotNull FireModeSpec fireMode,
        @NotNull ProjectileSpec projectile,
        @NotNull SpreadPatternSpec spreadPattern,
        @Nullable String shotSounds
) { }
