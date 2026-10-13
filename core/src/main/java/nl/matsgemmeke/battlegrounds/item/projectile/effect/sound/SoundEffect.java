package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.BaseProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;

public class SoundEffect extends BaseProjectileEffect {

    private final SoundEmitter soundEmitter;
    private final SoundTemplate soundTemplate;

    @Inject
    public SoundEffect(SoundEmitter soundEmitter, @Assisted SoundTemplate soundTemplate) {
        this.soundEmitter = soundEmitter;
        this.soundTemplate = soundTemplate;
    }

    @Override
    public void performEffect(Projectile projectile) {
        soundEmitter.playSoundTemplate(soundTemplate, projectile::getLocation);
    }
}
