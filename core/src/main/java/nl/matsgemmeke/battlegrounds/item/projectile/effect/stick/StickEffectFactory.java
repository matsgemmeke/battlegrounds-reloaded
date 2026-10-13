package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;

public interface StickEffectFactory {

    ProjectileEffect create(SoundTemplate soundTemplate);
}
