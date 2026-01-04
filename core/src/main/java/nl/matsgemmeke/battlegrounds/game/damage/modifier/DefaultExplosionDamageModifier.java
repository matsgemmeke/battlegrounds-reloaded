package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;

public class DefaultExplosionDamageModifier implements DamageModifier {

    private static final double DEFAULT_EXPLOSION_DAMAGE = 0.0;

    @Override
    public DamageContext apply(DamageContext damageContext) {
        if (damageContext.damage().type() != DamageType.EXPLOSIVE_DAMAGE) {
            return damageContext;
        }

        return damageContext.modifyDamageAmount(DEFAULT_EXPLOSION_DAMAGE);
    }
}
