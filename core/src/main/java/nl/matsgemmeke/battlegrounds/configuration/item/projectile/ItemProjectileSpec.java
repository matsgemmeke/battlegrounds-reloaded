package nl.matsgemmeke.battlegrounds.configuration.item.projectile;

import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class ItemProjectileSpec extends ProjectileSpec {

    @Required
    public ItemSpec item;

    @Required
    public Double velocity;

    @Required
    public Integer pickupDelay;
}
