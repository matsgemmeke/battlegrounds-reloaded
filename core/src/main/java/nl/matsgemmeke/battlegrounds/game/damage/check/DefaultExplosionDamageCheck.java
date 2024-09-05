package nl.matsgemmeke.battlegrounds.game.damage.check;

import nl.matsgemmeke.battlegrounds.game.damage.DamageCause;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import org.jetbrains.annotations.NotNull;

public class DefaultExplosionDamageCheck implements DamageCheck {

    private static final double DEFAULT_EXPLOSION_DAMAGE = 0.0;

    public void process(@NotNull DamageEvent event) {
        if (event.getCause() != DamageCause.DEFAULT_EXPLOSION) {
            return;
        }

        event.setDamage(DEFAULT_EXPLOSION_DAMAGE);
    }
}
