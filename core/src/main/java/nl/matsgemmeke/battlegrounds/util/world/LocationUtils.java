package nl.matsgemmeke.battlegrounds.util.world;

import org.bukkit.Location;
import org.bukkit.World;

public final class LocationUtils {

    private static final double BLOCK_CENTER_OFFSET = 0.5;

    public static Location getCenterLocation(Location original) {
        World world = original.getWorld();
        double x = Math.floor(original.getX()) + BLOCK_CENTER_OFFSET;
        double y = original.getY();
        double z = Math.floor(original.getZ()) + BLOCK_CENTER_OFFSET;
        float yaw = original.getYaw();
        float pitch = original.getPitch();
        return new Location(world, x, y, z, yaw, pitch);
    }
}
