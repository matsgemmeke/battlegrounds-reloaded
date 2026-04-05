package nl.matsgemmeke.battlegrounds.item.deploy.state;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;

/**
 * A deployment state in which the deployable item is primed to activate, but not deployed yet.
 */
public class PrimedState implements DeploymentState {

    @Override
    public DeploymentState processAction(Deployment deployment, DeploymentResult result) {
        return new DeployedState();
    }
}
