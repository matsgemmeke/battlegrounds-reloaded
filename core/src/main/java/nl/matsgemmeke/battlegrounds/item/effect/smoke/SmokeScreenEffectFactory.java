package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.jetbrains.annotations.NotNull;

public interface SmokeScreenEffectFactory {

    @NotNull
    ItemEffect create(@NotNull SmokeScreenProperties properties,
                      @NotNull AudioEmitter audioEmitter,
                      @NotNull CollisionDetector collisionDetector);
}
