package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.jetbrains.annotations.NotNull;

public interface CombustionEffectFactory {

    @NotNull
    ItemEffect create(@NotNull CombustionProperties properties,
                      @NotNull RangeProfile rangeProfile,
                      @NotNull AudioEmitter audioEmitter,
                      @NotNull CollisionDetector collisionDetector,
                      @NotNull TargetFinder targetFinder);
}
