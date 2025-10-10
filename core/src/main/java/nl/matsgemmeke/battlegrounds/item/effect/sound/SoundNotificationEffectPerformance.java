package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundNotificationEffectPerformance extends BaseItemEffectPerformance {

    private final List<GameSound> notificationSounds;

    public SoundNotificationEffectPerformance(List<GameSound> notificationSounds) {
        this.notificationSounds = notificationSounds;
    }

    @Override
    public boolean isPerforming() {
        // A sound notification effect is instant, therefore this effect will never perform for a longer period of time
        return false;
    }

    @Override
    public void perform(ItemEffectContext context) {
        Entity entity = context.entity();

        // Playing sounds is only possible for players
        if (!(entity instanceof Player player)) {
            return;
        }

        for (GameSound sound : notificationSounds) {
            player.playSound(player.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }
}
