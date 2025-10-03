package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;

import java.util.Set;

public class SoundNotificationEffectNew extends BaseItemEffectNew {

    private Set<GameSound> notificationSounds;

    public void setNotificationSounds(Set<GameSound> notificationSounds) {
        this.notificationSounds = notificationSounds;
    }

    @Override
    public ItemEffectPerformance start(ItemEffectContext context) {
        if (notificationSounds == null) {
            throw new ItemEffectPerformanceException("Unable to perform sound notification effect: notification sounds not set");
        }

        SoundNotificationEffectPerformance performance = new SoundNotificationEffectPerformance(notificationSounds);

        this.startPerformance(performance, context);
        return performance;
    }
}
