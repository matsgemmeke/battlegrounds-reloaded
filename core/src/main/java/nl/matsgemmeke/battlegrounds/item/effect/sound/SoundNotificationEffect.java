package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;

import java.util.List;

public class SoundNotificationEffect extends BaseItemEffect {

    private List<GameSound> notificationSounds;

    public void setNotificationSounds(List<GameSound> notificationSounds) {
        this.notificationSounds = notificationSounds;
    }

    @Override
    public void startPerformance(ItemEffectContext context) {
        if (notificationSounds == null) {
            throw new ItemEffectPerformanceException("Unable to perform sound notification effect: notification sounds not set");
        }

        SoundNotificationEffectPerformance performance = new SoundNotificationEffectPerformance(notificationSounds);

        this.startPerformance(performance, context);
    }
}
