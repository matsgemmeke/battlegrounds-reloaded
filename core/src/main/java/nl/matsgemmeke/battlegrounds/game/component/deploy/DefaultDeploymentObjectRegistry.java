package nl.matsgemmeke.battlegrounds.game.component.deploy;

import nl.matsgemmeke.battlegrounds.game.storage.DeploymentObjectStorage;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;

public class DefaultDeploymentObjectRegistry implements DeploymentObjectRegistry {

    @NotNull
    private DeploymentObjectStorage deploymentObjectStorage;

    public DefaultDeploymentObjectRegistry(@NotNull DeploymentObjectStorage deploymentObjectStorage) {
        this.deploymentObjectStorage = deploymentObjectStorage;
    }

    @NotNull
    public void registerDeploymentObject(@NotNull DeploymentObject deploymentObject) {
        deploymentObjectStorage.addDeploymentObject(deploymentObject);
    }
}
