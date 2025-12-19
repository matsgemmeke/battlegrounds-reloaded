package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * An item effect source with a fixed location.
 */
public class StaticItemEffectSource implements ItemEffectSource {

    private static final Vector ZERO = new Vector();

    private final Location location;
    private final World world;

    public StaticItemEffectSource(Location location, World world) {
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
