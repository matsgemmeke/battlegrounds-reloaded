package nl.matsgemmeke.battlegrounds.game.component.deploy;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;

public interface DeploymentObjectRegistry {

    @NotNull
    void registerDeploymentObject(@NotNull DeploymentObject deploymentObject);
}
