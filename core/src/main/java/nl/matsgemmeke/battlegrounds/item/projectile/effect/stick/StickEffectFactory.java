package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;

public interface StickEffectFactory {

    ProjectileEffect create(SoundSequence soundSequence);
}
