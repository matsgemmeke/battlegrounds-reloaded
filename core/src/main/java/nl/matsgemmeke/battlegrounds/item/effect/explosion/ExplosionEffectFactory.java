package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface ExplosionEffectFactory {

    ItemEffect create(ExplosionProperties properties, RangeProfile rangeProfile);
}
