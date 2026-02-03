package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;

/**
 * Describes the result of a deployment performance.
 *
 * @param deployer         the deployer
 * @param deploymentObject the produced deployment object
 * @param actor            the actor used for activating triggers and effects
 * @param cooldown         the cooldown the deployer should not be able to perform another deployment
 */
public record DeploymentResult(Deployer deployer, DeploymentObject deploymentObject, Actor actor, long cooldown) {
}
