package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import org.jetbrains.annotations.NotNull;

public interface DeploymentHandlerFactory {

    @NotNull
    DeploymentHandler create(@NotNull DeploymentProperties deploymentProperties, @NotNull AudioEmitter audioEmitter, @NotNull Effect effect);
}
