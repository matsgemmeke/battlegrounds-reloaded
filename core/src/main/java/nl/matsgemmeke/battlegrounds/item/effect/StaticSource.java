package nl.matsgemmeke.battlegrounds.item.effect;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class StaticSource implements ItemEffectSource {

    private static final Vector ZERO = new Vector();

    private final Location location;
    private final World world;

    public StaticSource(Location location, World world) {
        this.location = location;
        this.world = world;
    }

    public boolean exists() {
        return false;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    public Vector getVelocity() {
        return ZERO;
    }

    @NotNull
    public World getWorld() {
        return world;
    }

    public void remove() {
        // No-op because a static location cannot be removed
    }
}
