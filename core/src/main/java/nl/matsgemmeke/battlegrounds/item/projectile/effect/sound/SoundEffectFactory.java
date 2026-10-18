package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;

public interface SoundEffectFactory {

    ProjectileEffect create(SoundSequence soundSequence);
}
