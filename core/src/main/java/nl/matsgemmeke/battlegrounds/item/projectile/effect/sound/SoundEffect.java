package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.BaseProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;

public class SoundEffect extends BaseProjectileEffect {

    private final SoundEmitter soundEmitter;
    private final SoundSequence soundSequence;

    @Inject
    public SoundEffect(SoundEmitter soundEmitter, @Assisted SoundSequence soundSequence) {
        this.soundEmitter = soundEmitter;
        this.soundSequence = soundSequence;
    }

    @Override
    public void performEffect(Projectile projectile) {
        soundEmitter.playSoundSequence(soundSequence, projectile::getLocation);
    }
}
