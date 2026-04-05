package nl.matsgemmeke.battlegrounds.item.deploynew.state;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploynew.Deployment;

/**
 * A deployment state in which the deployable item is primed to activate, but not deployed yet.
 */
public class PrimedState implements DeploymentState {

    @Override
    public DeploymentState processAction(Deployment deployment, DeploymentResult result) {
        return new DeployedState();
    }
}
