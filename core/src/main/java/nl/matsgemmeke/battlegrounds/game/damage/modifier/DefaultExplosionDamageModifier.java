package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;

public class DefaultExplosionDamageModifier implements DamageModifier {

    private static final double DEFAULT_EXPLOSION_DAMAGE = 0.0;

    @Override
    public void apply(DamageEvent event) {
        if (event.getDamageType() != DamageType.EXPLOSIVE_DAMAGE) {
            return;
        }

        event.setDamage(DEFAULT_EXPLOSION_DAMAGE);
    }
}
