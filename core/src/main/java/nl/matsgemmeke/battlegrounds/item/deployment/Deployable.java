package nl.matsgemmeke.battlegrounds.item.deployment;

import org.jetbrains.annotations.NotNull;

public interface Deployable {

    boolean isDeployed();

    void onDeploy(@NotNull DeployableObject deployableObject);
}
