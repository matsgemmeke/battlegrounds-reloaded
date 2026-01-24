package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Represents a physical, observable object that exists in the game world.
 * <p>
 * An {@code Actor} is a tracked world object that has a position and a lifecycle whose state may change over time (for
 * example, by moving or disappearing). Features such as item effects and triggers may observe an {@code Actor} and
 * react to its presence, position, or movement.
 * <p>
 * Typical implementations include players, entities (e.g. arrows or projectiles), blocks, or other physical objects
 * that can be located in the world.
 */
public interface Actor {

    /**
     * Returns whether this actor currently exists and can be observed.
     *
     * @return {@code true} if the actor exists, {@code false} otherwise
     */
    boolean exists();

    /**
     * Returns the current location of this actor.
     *
     * @return the actor's location
     */
    Location getLocation();

    /**
     * Returns the current velocity of this actor.
     *
     * @return the velocity's location
     */
    Vector getVelocity();

    /**
     * Returns the world this actor exists in.
     *
     * @return the actor's world
     */
    World getWorld();
}
