package nl.matsgemmeke.battlegrounds.item.deploynew.state;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploynew.Deployment;
import nl.matsgemmeke.battlegrounds.item.deploynew.DeploymentAction;

/**
 * A deployment state in which the deployable item is idle and not deployed.
 */
public class IdleState implements DeploymentState {

    @Override
    public DeploymentState processAction(Deployment deployment, DeploymentAction action) {
        Deployer deployer = action.deployer();
        Actor actor = action.actor();

        deployment.setPerforming(true);
        deployment.startTriggerExecutors(deployer, actor);

        if (action.deploymentObject().isPhysical()) {
            deployment.scheduleDeploymentCooldown(deployer, action.cooldown());

            return new DeployedState();
        } else {
            return new PrimedState();
        }
    }
}
