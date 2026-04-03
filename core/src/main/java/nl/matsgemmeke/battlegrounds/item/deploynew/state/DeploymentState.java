package nl.matsgemmeke.battlegrounds.item.deploynew.state;

import nl.matsgemmeke.battlegrounds.item.deploynew.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploynew.DeploymentAction;

public interface DeploymentState {

    DeploymentState processAction(Deployment deployment, DeploymentAction action);
}
