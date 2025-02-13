package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import org.jetbrains.annotations.NotNull;

public interface GunFireSimulationEffectFactory {

    @NotNull
    ItemEffect create(@NotNull ItemEffectActivation effectActivation,
                      @NotNull AudioEmitter audioEmitter,
                      @NotNull GunInfoProvider gunInfoProvider,
                      @NotNull GunFireSimulationProperties properties);
}
