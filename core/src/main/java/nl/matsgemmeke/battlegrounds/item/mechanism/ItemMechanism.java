package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.item.Item;

/**
 * A mechanism component of an {@link Item}. Produces a certain effect when activated.
 */
public interface ItemMechanism {

    /**
     * Activates the mechanism.
     */
    void activate();
}
