package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;

public interface SmokeScreenEffectPerformanceFactory {

    ItemEffectPerformance create(SmokeScreenProperties properties);
}
