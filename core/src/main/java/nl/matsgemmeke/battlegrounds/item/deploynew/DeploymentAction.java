package nl.matsgemmeke.battlegrounds.item.deploynew;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;

/**
 * Describes a deployment action.
 *
 * @param deployer         the deployer
 * @param deploymentObject the produced deployment object
 * @param actor            the actor used for activating triggers and effects
 * @param cooldown         the cooldown the deployer should not be able to perform another deployment
 */
public record DeploymentAction(Deployer deployer, DeploymentObject deploymentObject, Actor actor, long cooldown) {
}
