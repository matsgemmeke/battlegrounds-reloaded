package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.Matchable;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.jetbrains.annotations.NotNull;

/**
 * A tool used by items to manually activate effects.
 */
public interface Activator extends Matchable {

    /**
     * Gets whether the activator is prepared and ready to be used.
     *
     * @return whether the activator is ready
     */
    boolean isReady();

    /**
     * Prepares the activator for a specific deployer entity.
     *
     * @param deployer the deployer who prepares the activator
     */
    void prepare(@NotNull Deployer deployer);

    /**
     * Removes the activator item from the current holder.
     *
     * @return whether the activator was removed
     */
    boolean remove();
}
