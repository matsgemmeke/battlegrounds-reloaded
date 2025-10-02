package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;

public interface GunFireSimulationEffectPerformanceFactory {

    ItemEffectPerformance create(GunFireSimulationProperties properties);
}
