package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoundNotificationEffect extends BaseItemEffect {

    @NotNull
    private Iterable<GameSound> sounds;

    public SoundNotificationEffect(@NotNull ItemEffectActivation effectActivation, @NotNull Iterable<GameSound> sounds) {
        super(effectActivation);
        this.sounds = sounds;
    }

    public void perform(@NotNull ItemEffectContext context) {
        ItemHolder holder = context.getHolder();

        // Playing sounds is only possible for players
        if (!(holder.getEntity() instanceof Player player)) {
            return;
        }

        for (GameSound sound : sounds) {
            player.playSound(player.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }
}
