package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;

import java.util.List;

public record TrailProperties(
        ParticleEffect particleEffect,
        Long delay,
        List<Long> intervals,
        Integer maxActivations
) { }
