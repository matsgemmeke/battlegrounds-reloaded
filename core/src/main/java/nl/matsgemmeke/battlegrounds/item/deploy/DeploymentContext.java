package nl.matsgemmeke.battlegrounds.item.deploy;

import org.bukkit.entity.Entity;

/**
 * Describes the context of a deployment.
 *
 * @param entity           the entity belonging to the deployer
 * @param deployer         the deployer
 * @param deploymentObject the produced deployment object
 * @param cooldown         the cooldown the deployer should not be able to perform another deployment
 */
public record DeploymentContext(
        @Deprecated Entity entity,
        Deployer deployer,
        DeploymentObject deploymentObject,
        long cooldown
) {
}
