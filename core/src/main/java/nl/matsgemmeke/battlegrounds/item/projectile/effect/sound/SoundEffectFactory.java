package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;

import java.util.List;

public interface SoundEffectFactory {

    ProjectileEffect create(List<GameSound> sounds);
}
