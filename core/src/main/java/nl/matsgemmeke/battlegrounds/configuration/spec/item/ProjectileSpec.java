package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.Nullable;

public record ProjectileSpec(
        @Nullable ParticleEffectSpec trajectoryParticleEffect
) { }
