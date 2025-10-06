package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Set;

public class SoundNotificationEffectPerformance extends BaseItemEffectPerformance {

    private final Set<GameSound> notificationSounds;

    public SoundNotificationEffectPerformance(Set<GameSound> notificationSounds) {
        this.notificationSounds = notificationSounds;
    }

    @Override
    public boolean isPerforming() {
        // A sound notification effect is instant, therefore this effect will never perform for a longer period of time
        return false;
    }

    @Override
    public void perform(ItemEffectContext context) {
        Entity entity = context.getEntity();

        // Playing sounds is only possible for players
        if (!(entity instanceof Player player)) {
            return;
        }

        for (GameSound sound : notificationSounds) {
            player.playSound(player.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }

    @Override
    public void cancel() {
        triggerRuns.forEach(TriggerRun::cancel);
    }
}
