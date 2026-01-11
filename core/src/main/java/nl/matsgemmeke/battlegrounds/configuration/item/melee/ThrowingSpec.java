package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ThrowingSpec {

    @Required
    public Integer throwsAmount;

    @Required
    public ProjectileSpec projectile;
}
