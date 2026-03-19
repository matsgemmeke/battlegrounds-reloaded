package nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class BounceEffectSpec extends ProjectileEffectSpec {

    @Required
    public Double horizontalFriction;

    @Required
    public Double verticalFriction;

    @Required
    public Integer maxActivations;
}
