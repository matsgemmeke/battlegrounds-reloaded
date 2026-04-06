package nl.matsgemmeke.battlegrounds.item.deploy.state;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;

/**
 * A deployment state in which the deployable item is idle and not deployed.
 */
public class IdleState implements DeploymentState {

    @Override
    public DeploymentState processAction(Deployment deployment, DeploymentResult result) {
        Deployer deployer = result.deployer();
        Actor actor = result.actor();

        deployment.startTriggerExecutors(deployer, actor);

        if (result.deploymentObject().isPhysical()) {
            deployment.setDeployed(true);
            deployment.scheduleDeploymentCooldown(deployer, result.cooldown());

            return new DeployedState();
        } else {
            return new PrimedState();
        }
    }
}
