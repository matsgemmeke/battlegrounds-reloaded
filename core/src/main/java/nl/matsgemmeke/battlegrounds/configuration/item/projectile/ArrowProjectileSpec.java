package nl.matsgemmeke.battlegrounds.configuration.item.projectile;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ArrowProjectileSpec extends ProjectileSpec {

    @Required
    public Double velocity;
}
