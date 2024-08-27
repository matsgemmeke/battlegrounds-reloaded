package nl.matsgemmeke.battlegrounds.item.deployment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An item capable of creating and holding references to {@link DeployedObject}s.
 */
public interface DeployableSource {

    @NotNull
    List<DeployedObject> getDeployedObjects();

    void onDeploy(@NotNull DeployedObject object);
}
