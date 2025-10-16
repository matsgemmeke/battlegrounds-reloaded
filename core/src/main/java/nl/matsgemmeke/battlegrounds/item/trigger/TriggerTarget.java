package nl.matsgemmeke.battlegrounds.item.trigger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Represents an object that can be monitored by a {@link Trigger}.
 * <p>
 * A {@code TriggerTarget} provides a condition or state that a {@link Trigger} checks to determine whether it should
 * activate.
 */
public interface TriggerTarget {

    /**
     * Gets whether the target exists.
     *
     * @return whether the target exists
     */
    boolean exists();

    /**
     * Gets the location of the trigger source.
     *
     * @return the trigger source location
     */
    Location getLocation();

    Vector getVelocity();

    World getWorld();
}
