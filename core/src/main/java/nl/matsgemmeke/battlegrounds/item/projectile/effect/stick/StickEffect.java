package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.BaseProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.bukkit.util.Vector;

public class StickEffect extends BaseProjectileEffect {

    private static final Vector ZERO = new Vector(0, 0, 0);

    private final SoundEmitter soundEmitter;
    private final SoundSequence soundSequence;

    @Inject
    public StickEffect(SoundEmitter soundEmitter, @Assisted SoundSequence soundSequence) {
        this.soundEmitter = soundEmitter;
        this.soundSequence = soundSequence;
    }

    public void performEffect(Projectile projectile) {
        soundEmitter.playSoundSequence(soundSequence, projectile::getLocation);

        projectile.setGravity(false);
        projectile.setVelocity(ZERO);
    }
}
