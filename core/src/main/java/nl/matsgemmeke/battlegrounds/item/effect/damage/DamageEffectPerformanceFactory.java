package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;

public interface DamageEffectPerformanceFactory {

    ItemEffectPerformance create(DamageProperties properties);
}
