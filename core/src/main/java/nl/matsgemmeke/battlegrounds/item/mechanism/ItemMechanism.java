package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.item.Item;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A mechanism component of an {@link Item}. Produces a certain effect when activated.
 */
public interface ItemMechanism {

    /**
     * Activates the mechanism.
     *
     * @param location the location to run the mechanism
     */
    void activate(@NotNull Location location);
}
