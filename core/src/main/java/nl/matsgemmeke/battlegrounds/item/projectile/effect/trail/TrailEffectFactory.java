package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.jetbrains.annotations.NotNull;

public interface TrailEffectFactory {

    @NotNull
    ProjectileEffect create(@NotNull TrailProperties properties);
}
