package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ProjectileSpec(
        @NotNull String type,
        @Nullable String shotSounds,
        @Nullable ParticleEffectSpec trajectoryParticleEffect
) { }
