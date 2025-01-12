package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.Matchable;
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
     * Prepares the activator by a specific item holder.
     *
     * @param holder the item holder who prepares the activator
     */
    void prepare(@NotNull ItemHolder holder);

    /**
     * Removes the activator item from the current holder.
     *
     * @return whether the activator was removed
     */
    boolean remove();
}
