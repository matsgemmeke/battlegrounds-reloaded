package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Describes the context of a deployment.
 *
 * @param entity           the entity belonging to the deployer
 * @param effectSource     the source for the item effect when it gets activated
 * @param deployer         the deployer
 * @param deploymentObject the produced deployment object or null when the deployment is primed but not yet released by
 *                         the deployer
 */
public record DeploymentContext(
        @Deprecated Entity entity,
        ItemEffectSource effectSource,
        Deployer deployer,
        @Nullable DeploymentObject deploymentObject
) {
}
