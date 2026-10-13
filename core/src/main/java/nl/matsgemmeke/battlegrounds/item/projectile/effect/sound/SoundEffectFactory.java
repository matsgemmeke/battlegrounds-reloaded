package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;

public interface SoundEffectFactory {

    ProjectileEffect create(SoundTemplate soundTemplate);
}
