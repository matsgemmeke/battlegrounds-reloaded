package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface GunFireSimulationEffectFactory {

    ItemEffect create(GunFireSimulationProperties properties);
}
