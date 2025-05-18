package nl.matsgemmeke.battlegrounds.item.projectile.effect.bounce;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface BounceEffectFactory {

    @NotNull
    ProjectileEffect create(@NotNull BounceProperties properties, @NotNull Set<Trigger> triggers);
}
