package nl.matsgemmeke.battlegrounds.game.component.info.deploy;

import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DeploymentInfoProvider {

    @NotNull
    List<DeploymentObject> getAllDeploymentObjects();

    @Nullable
    DeployableItem getDeployableItem(@NotNull DeploymentObject deploymentObject);
}
