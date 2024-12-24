package nl.matsgemmeke.battlegrounds.item.deploy;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DeployableItem {

    void addDeploymentObject(@NotNull DeploymentObject deploymentObject);

    @NotNull
    List<DeploymentObject> getDeploymentObjects();

    void removeDeploymentObject(@NotNull DeploymentObject deploymentObject);
}
