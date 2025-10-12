package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;

public interface CombustionEffectPerformanceFactory {

    ItemEffectPerformance create(CombustionProperties properties);
}
