package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import org.jetbrains.annotations.NotNull;

public record TrailProperties(
        @NotNull ParticleEffectProperties particleEffect,
        long checkDelay,
        long checkPeriod
) {}
