package nl.matsgemmeke.battlegrounds.item.deploynew.state;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploynew.Deployment;

public interface DeploymentState {

    DeploymentState processAction(Deployment deployment, DeploymentResult result);
}
