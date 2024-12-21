package nl.matsgemmeke.battlegrounds.game.storage;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DeploymentObjectStorage {

    @NotNull
    private List<DeploymentObject> deploymentObjects;

    public DeploymentObjectStorage() {
        this.deploymentObjects = new ArrayList<>();
    }

    public void addDeploymentObject(@NotNull DeploymentObject deploymentObject) {
        deploymentObjects.add(deploymentObject);
    }

    @NotNull
    public List<DeploymentObject> getDeploymentObjects() {
        return deploymentObjects;
    }
}
