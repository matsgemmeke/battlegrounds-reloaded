package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import org.jetbrains.annotations.NotNull;

public interface SmokeScreenEffectFactory {

    @NotNull
    ItemEffect create(@NotNull ItemEffectActivation effectActivation,
                      @NotNull SmokeScreenProperties properties,
                      @NotNull AudioEmitter audioEmitter,
                      @NotNull CollisionDetector collisionDetector);
}
