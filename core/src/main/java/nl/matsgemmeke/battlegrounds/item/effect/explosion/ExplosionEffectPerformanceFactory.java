package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;

public interface ExplosionEffectPerformanceFactory {

    ItemEffectPerformance create(ExplosionProperties properties);
}
