package nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class TrailEffectSpec extends ProjectileEffectSpec {

    @Required
    public Integer maxActivations;

    @Required
    @Valid
    public ParticleEffectSpec particleEffect;
}
