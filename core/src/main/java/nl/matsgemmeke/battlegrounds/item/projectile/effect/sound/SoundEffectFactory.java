package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.jetbrains.annotations.NotNull;

public interface SoundEffectFactory {

    @NotNull
    ProjectileEffect create(@NotNull SoundProperties properties);
}
