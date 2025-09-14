package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;

import java.util.List;

public interface StickEffectFactory {

    ProjectileEffect create(List<GameSound> sounds);
}
