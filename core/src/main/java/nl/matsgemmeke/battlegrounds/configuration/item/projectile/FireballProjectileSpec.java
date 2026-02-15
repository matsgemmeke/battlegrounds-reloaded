package nl.matsgemmeke.battlegrounds.configuration.item.projectile;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class FireballProjectileSpec extends ProjectileSpec {

    @Required
    public Double velocity;
}
