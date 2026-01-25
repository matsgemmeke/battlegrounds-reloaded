package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * An actor with a fixed location and world.
 */
public class StaticActor implements Actor {

    private static final Vector ZERO = new Vector();

    private final Location location;
    private final World world;

    public StaticActor(Location location, World world) {
        this.location = location;
        this.world = world;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Vector getVelocity() {
        return ZERO;
    }

    @Override
    public World getWorld() {
        return world;
    }
}
