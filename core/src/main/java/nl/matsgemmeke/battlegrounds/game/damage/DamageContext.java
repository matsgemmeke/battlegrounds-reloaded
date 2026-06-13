package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.entity.damage.Damage;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;

public record DamageContext(
        DamageSource source,
        DamageTarget target,
        String itemName,
        Damage damage,
        double distance
) {
}
