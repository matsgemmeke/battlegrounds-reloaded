package nl.matsgemmeke.battlegrounds.item.deploy.state;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;

public interface DeploymentState {

    DeploymentState processAction(Deployment deployment, DeploymentResult result);
}
