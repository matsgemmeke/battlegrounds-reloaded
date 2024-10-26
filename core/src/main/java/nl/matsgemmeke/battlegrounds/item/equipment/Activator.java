package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.Matchable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A tool used by {@link Equipment} items to manually activate effects.
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
     * @param values the template values used to construct the activator
     */
    void prepare(@NotNull ItemHolder holder, @NotNull Map<String, Object> values);
}
