package nl.matsgemmeke.battlegrounds.item.deploy;

/**
 * Describes the result of a deployment performance.
 *
 * @param deployer         the deployer
 * @param deploymentObject the produced deployment object
 * @param cooldown         the cooldown the deployer should not be able to perform another deployment
 */
public record DeploymentResult(Deployer deployer, DeploymentObject deploymentObject, long cooldown) {
}
