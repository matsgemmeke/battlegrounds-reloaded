package nl.matsgemmeke.battlegrounds.game.component.info.deploy;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DeploymentInfoProvider {

    @NotNull
    List<DeploymentObject> getAllDeploymentObjects();
}
