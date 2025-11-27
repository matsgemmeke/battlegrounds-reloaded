package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEventNew;

public interface DamageModifier {

    void apply(DamageEvent event);

    DamageEventNew apply(DamageEventNew damageEvent);
}
