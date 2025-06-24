package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import org.jetbrains.annotations.Nullable;

public record ProjectileSpec(
        @Nullable ParticleEffectSpec trajectoryParticleEffect
) { }
