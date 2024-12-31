package nl.matsgemmeke.battlegrounds.game.damage.check;

import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.jetbrains.annotations.NotNull;

public class NegateDefaultExplosionDamageCheck implements DamageCheck {

    private static final double DEFAULT_EXPLOSION_DAMAGE = 0.0;

    public void process(@NotNull DamageEvent event) {
        if (event.getDamageType() != DamageType.EXPLOSIVE_DAMAGE) {
            return;
        }

        event.setDamage(DEFAULT_EXPLOSION_DAMAGE);
    }
}
