package nl.matsgemmeke.battlegrounds.item.deploynew.state;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploynew.Deployment;

/**
 * A deployment state in which the deployable item is primed to activate, and deployed as a deployment object.
 */
public class DeployedState implements DeploymentState {

    @Override
    public DeployedState processAction(Deployment deployment, DeploymentResult result) {
        return this;
    }
}
