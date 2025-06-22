package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ShootingSpec(
        @NotNull FireModeSpec fireMode,
        @NotNull ProjectileSpec projectile,
        @Nullable String shotSounds
) { }
