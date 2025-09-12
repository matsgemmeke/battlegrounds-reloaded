package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface DamageEffectFactory {

    ItemEffect create(DamageProperties properties);
}
