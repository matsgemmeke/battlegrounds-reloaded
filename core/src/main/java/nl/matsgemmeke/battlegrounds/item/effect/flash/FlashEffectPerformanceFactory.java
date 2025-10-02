package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;

public interface FlashEffectPerformanceFactory {

    ItemEffectPerformance create(FlashProperties properties);
}
