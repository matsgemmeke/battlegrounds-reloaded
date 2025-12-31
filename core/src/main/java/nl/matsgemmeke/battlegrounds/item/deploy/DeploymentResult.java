package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;

/**
 * Describes the result of a deployment performance.
 *
 * @param deployer         the deployer
 * @param deploymentObject the produced deployment object
 * @param triggerTarget    the target used for activating triggers
 * @param cooldown         the cooldown the deployer should not be able to perform another deployment
 */
public record DeploymentResult(
        Deployer deployer,
        DeploymentObject deploymentObject,
        TriggerTarget triggerTarget,
        long cooldown
) {
}
