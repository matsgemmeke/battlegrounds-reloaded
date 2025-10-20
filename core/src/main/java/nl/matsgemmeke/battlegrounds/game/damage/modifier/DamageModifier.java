package nl.matsgemmeke.battlegrounds.game.damage.modifier;

import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;

public interface DamageModifier {

    void apply(DamageEvent event);
}
