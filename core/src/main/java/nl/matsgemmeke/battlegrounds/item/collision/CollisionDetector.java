package nl.matsgemmeke.battlegrounds.item.collision;

import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CollisionDetector {

    private static final double STEP_SIZE = 0.5;

    private final BlockCollisionChecker blockCollisionChecker;

    @Inject
    public CollisionDetector(BlockCollisionChecker blockCollisionChecker) {
        this.blockCollisionChecker = blockCollisionChecker;
    }

    /**
     * Detects if two locations have a clear line of sight of each other without any colliding blocks in between.
     *
     * @param start the first location
     * @param end   the second location
     * @return      whether the two given locations have a line of sight of each other
     */
    public boolean hasLineOfSight(Location start, Location end) {
        World world = start.getWorld();

        // Ensure both locations are in the same world
        if (world == null || world != end.getWorld()) {
            return false;
        }

        // Calculate direction vector and distance
        Vector direction = end.toVector().subtract(start.toVector());
        double distance = direction.length();

        // Normalize the direction to get unit steps
        direction.normalize();

        // Set the step size, e.g., 0.5 blocks per step for accuracy
        Vector step = direction.multiply(STEP_SIZE);
        // Iterate from start to end in steps along the vector
        Location current = start.clone();

        for (double traveled = 0; traveled < distance; traveled += STEP_SIZE) {
            // Check if the block at this location is solid
            if (world.getBlockAt(current.getBlockX(), current.getBlockY(), current.getBlockZ()).getType().isSolid()) {
                return false; // Line of sight is blocked
            }

            // Move to the next step along the line
            current.add(step);
        }

        // If no solid blocks were found, line of sight is clear
        return true;
    }

    /**
     * Verifies if the given location produces a collision with a block.
     *
     * @param location the location
     * @return whether a block collision is produced
     */
    public boolean producesBlockCollisionAt(Location location) {
        return blockCollisionChecker.isSolid(location.getBlock(), location);
    }
}
