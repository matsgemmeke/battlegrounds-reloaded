package nl.matsgemmeke.battlegrounds.configuration.item.projectile;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class ArrowProjectileSpec extends ProjectileSpec {

    @Required
    public Double velocity;
}
