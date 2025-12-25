package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;

public interface SoundNotificationEffectPerformanceFactory {

    ItemEffectPerformance create(SoundNotificationProperties properties);
}
