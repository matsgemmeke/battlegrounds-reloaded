package nl.matsgemmeke.battlegrounds.item.effect.sound;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;

import java.util.UUID;

public class SoundNotificationEffectPerformance extends BaseItemEffectPerformance {

    private final PlayerRegistry playerRegistry;
    private final SoundNotificationProperties properties;

    @Inject
    public SoundNotificationEffectPerformance(PlayerRegistry playerRegistry, @Assisted SoundNotificationProperties properties) {
        this.playerRegistry = playerRegistry;
        this.properties = properties;
    }

    @Override
    public boolean isPerforming() {
        // A sound notification effect is instant, therefore this effect will never perform for a longer period of time
        return false;
    }

    @Override
    public void start() {
        UUID damageSourceUniqueId = currentContext.getDamageSource().getUniqueId();
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(damageSourceUniqueId).orElse(null);

        if (gamePlayer == null) {
            return;
        }

        properties.notificationSounds().forEach(sound -> gamePlayer.playSound(gamePlayer.getLocation(), sound));
    }
}
