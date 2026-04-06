package nl.matsgemmeke.battlegrounds.item.deploy.state;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;

/**
 * A deployment state in which the deployable item is primed to activate, but not deployed yet.
 */
public class PrimedState implements DeploymentState {

    @Override
    public DeploymentState processAction(Deployment deployment, DeploymentResult result) {
        DeploymentObject deploymentObject = result.deploymentObject();

        if (!deploymentObject.isPhysical()) {
            return this;
        }

        deployment.setDeployed(true);
        deployment.replaceActor(result.actor());
        deployment.scheduleDeploymentCooldown(result.deployer(), result.cooldown());

        return new DeployedState();
    }
}
