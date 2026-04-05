package nl.matsgemmeke.battlegrounds.item.deploynew.state;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploynew.Deployment;

/**
 * A deployment state in which the deployable item is idle and not deployed.
 */
public class IdleState implements DeploymentState {

    @Override
    public DeploymentState processAction(Deployment deployment, DeploymentResult result) {
        Deployer deployer = result.deployer();
        Actor actor = result.actor();

        deployment.setPerforming(true);
        deployment.startTriggerExecutors(deployer, actor);

        if (result.deploymentObject().isPhysical()) {
            deployment.scheduleDeploymentCooldown(deployer, result.cooldown());

            return new DeployedState();
        } else {
            return new PrimedState();
        }
    }
}
