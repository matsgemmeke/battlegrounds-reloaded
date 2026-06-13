package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentContext;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;

import java.util.Optional;

public interface DeploymentAction {

    Optional<DeploymentResult> perform(DeploymentContext context);
}
