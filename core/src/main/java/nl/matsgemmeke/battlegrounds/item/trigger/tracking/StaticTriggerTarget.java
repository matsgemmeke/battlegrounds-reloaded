package nl.matsgemmeke.battlegrounds.item.trigger.tracking;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * A static trigger target with a fixed location and world.
 */
public class StaticTriggerTarget implements TriggerTarget {

    private static final Vector ZERO = new Vector();

    private final Location location;
    private final World world;

    public StaticTriggerTarget(Location location, World world) {
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
