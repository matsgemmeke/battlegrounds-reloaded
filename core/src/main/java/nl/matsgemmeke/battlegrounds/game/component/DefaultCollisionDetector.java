package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class DefaultCollisionDetector implements CollisionDetector {

    @NotNull
    private BlockCollisionChecker blockCollisionChecker;

    public DefaultCollisionDetector(@NotNull BlockCollisionChecker blockCollisionChecker) {
        this.blockCollisionChecker = blockCollisionChecker;
    }

    public boolean hasLineOfSight(@NotNull Location from, @NotNull Location to) {
        World world = from.getWorld();

        if (world == null || world != to.getWorld()) {
            return false;
        }

        // Trace the blocks from the starting location to the destination location
        double distance = from.distance(to);

        for (int i = 0; i < distance; i++) {
            double t = i / distance;

            int x = (int) (from.getX() + t * (to.getX() - from.getX()));
            int y = (int) (from.getY() + t * (to.getY() - from.getY()));
            int z = (int) (from.getZ() + t * (to.getZ() - from.getZ()));

            Block block = world.getBlockAt(x, y, z);

            if (!block.isPassable()) {
                return false;
            }
        }
        return true;
    }

    public boolean producesBlockCollisionAt(@NotNull Location location) {
        return blockCollisionChecker.isSolid(location.getBlock(), location);
    }
}
