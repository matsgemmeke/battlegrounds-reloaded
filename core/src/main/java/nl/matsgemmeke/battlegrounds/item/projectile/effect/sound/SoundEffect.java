package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.BaseProjectileEffect;

import java.util.List;

public class SoundEffect extends BaseProjectileEffect {

    private final AudioEmitter audioEmitter;
    private final List<GameSound> sounds;

    @Inject
    public SoundEffect(AudioEmitter audioEmitter, @Assisted List<GameSound> sounds) {
        this.audioEmitter = audioEmitter;
        this.sounds = sounds;
    }

    public void performEffect(Projectile projectile) {
        audioEmitter.playSounds(sounds, projectile.getLocation());
    }
}
