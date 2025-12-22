package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.EntityDamageEvent;

public interface DamageModifier {

    void apply(DamageEvent event);

    EntityDamageEvent apply(EntityDamageEvent damageEvent);

    DamageContext apply(DamageContext damageContext);
}
