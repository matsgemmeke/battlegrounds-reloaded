package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.jetbrains.annotations.NotNull;

public interface DeploymentHandlerFactory {

    @NotNull
    DeploymentHandler create(@NotNull ActivationProperties activationProperties, @NotNull AudioEmitter audioEmitter, @NotNull ItemEffect effect);
}
