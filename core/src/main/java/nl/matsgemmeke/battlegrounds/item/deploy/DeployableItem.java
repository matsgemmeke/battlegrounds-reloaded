package nl.matsgemmeke.battlegrounds.item.deploy;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DeployableItem {

    void activateDeployment(@NotNull Deployer deployer, @NotNull Entity deployerEntity);

    @NotNull
    List<DeploymentObject> getDeploymentObjects();

    boolean isActivatorReady();

    void onDeployDeploymentObject(@NotNull DeploymentObject deploymentObject);

    void onDestroyDeploymentObject(@NotNull DeploymentObject deploymentObject);
}
