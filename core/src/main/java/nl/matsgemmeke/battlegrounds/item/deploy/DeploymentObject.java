package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.entity.Identifiable;
import nl.matsgemmeke.battlegrounds.game.damage.Damageable;
import nl.matsgemmeke.battlegrounds.item.effect.EffectSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that is produced as a result of a deployment action.
 */
public interface DeploymentObject extends EffectSource, Damageable, Identifiable {

    /**
     * Gets the amount of ticks this deployment object will cause the deployer to have a cooldown before they can
     * perform another deployment.
     *
     * @return the cooldown duration in ticks
     */
    long getCooldown();

    /**
     * Gets whether the deployment object has been deployed or is still held by the deployer.
     *
     * @return whether the deployment object is deployed
     */
    boolean isDeployed();

    /**
     * Gets whether the object matches with a given entity. Returns {@code true} if the object encapsulates the given
     * entity, otherwise {@code false}.
     *
     * @param entity the entity
     * @return whether the object matches with the entity
     */
    boolean matchesEntity(@NotNull Entity entity);
}
