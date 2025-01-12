package nl.matsgemmeke.battlegrounds.item.deploy;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DeployableItem {

    @NotNull
    List<DeploymentObject> getDeploymentObjects();

    void onDeployDeploymentObject(@NotNull DeploymentObject deploymentObject);

    void onDestroyDeploymentObject(@NotNull DeploymentObject deploymentObject);
}
