package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.item.ParticleEffect;
import org.jetbrains.annotations.NotNull;

public record TrailProperties(
        @NotNull ParticleEffect particleEffect,
        long checkDelay,
        long checkPeriod
) {}
