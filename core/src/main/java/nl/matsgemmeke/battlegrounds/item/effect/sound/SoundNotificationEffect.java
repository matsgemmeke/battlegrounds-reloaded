package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoundNotificationEffect extends BaseItemEffect {

    @NotNull
    private Iterable<GameSound> sounds;

    public SoundNotificationEffect(@NotNull Iterable<GameSound> sounds) {
        this.sounds = sounds;
    }

    public void perform(@NotNull ItemEffectContext context) {
        Entity entity = context.getEntity();

        // Playing sounds is only possible for players
        if (!(entity instanceof Player player)) {
            return;
        }

        for (GameSound sound : sounds) {
            player.playSound(player.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }
}
