package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.jetbrains.annotations.NotNull;

public interface StickEffectFactory {

    @NotNull
    ProjectileEffect create(@NotNull StickProperties properties, @NotNull AudioEmitter audioEmitter);
}
