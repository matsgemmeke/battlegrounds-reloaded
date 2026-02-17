package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;

public interface DamageModifier {

    DamageContext apply(DamageContext damageContext);
}
