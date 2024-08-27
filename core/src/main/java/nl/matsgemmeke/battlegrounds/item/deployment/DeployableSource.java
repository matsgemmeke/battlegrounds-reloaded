package nl.matsgemmeke.battlegrounds.item.deployment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An item capable of creating and holding references to {@link Deployable} objects.
 */
public interface DeployableSource {

    @NotNull
    List<Deployable> getDeployedObjects();

    void onDeploy(@NotNull Deployable object);
}
