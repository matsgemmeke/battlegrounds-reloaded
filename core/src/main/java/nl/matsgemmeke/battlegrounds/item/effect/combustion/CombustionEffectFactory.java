package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface CombustionEffectFactory {

    ItemEffect create(CombustionProperties properties, RangeProfile rangeProfile);
}
