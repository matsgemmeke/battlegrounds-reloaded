package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.BaseProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.bukkit.util.Vector;

public class StickEffect extends BaseProjectileEffect {

    private static final Vector ZERO = new Vector(0, 0, 0);

    private final SoundEmitter soundEmitter;
    private final SoundTemplate soundTemplate;

    @Inject
    public StickEffect(SoundEmitter soundEmitter, @Assisted SoundTemplate soundTemplate) {
        this.soundEmitter = soundEmitter;
        this.soundTemplate = soundTemplate;
    }

    public void performEffect(Projectile projectile) {
        soundEmitter.playSoundTemplate(soundTemplate, projectile::getLocation);

        projectile.setGravity(false);
        projectile.setVelocity(ZERO);
    }
}
