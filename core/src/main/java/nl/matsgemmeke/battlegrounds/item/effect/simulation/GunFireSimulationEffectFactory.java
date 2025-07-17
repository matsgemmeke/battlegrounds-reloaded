package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import org.jetbrains.annotations.NotNull;

public interface GunFireSimulationEffectFactory {

    @NotNull
    Effect create(@NotNull AudioEmitter audioEmitter,
                  @NotNull GunInfoProvider gunInfoProvider,
                  @NotNull GunFireSimulationProperties properties);
}
