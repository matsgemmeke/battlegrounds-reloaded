package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.projectile.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ThrowingSpec {

    @Required
    public ProjectileSpec projectile;
}
