package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * An item effect source with a fixed location.
 */
public class StaticItemEffectSource implements ItemEffectSource {

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
    public World getWorld() {
        return world;
    }
}
