package nl.matsgemmeke.battlegrounds.game.component.collision;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A façade interface whose responsibility is to detect possible collision in a game context.
 */
public interface CollisionDetector {

    /**
     * Detects if two locations have a clear line of sight of each other without any colliding blocks in between.
     *
     * @param from the first location
     * @param to the second location
     * @return whether the two given locations have a line of sight of each other
     */
    boolean hasLineOfSight(@NotNull Location from, @NotNull Location to);

    /**
     * Verifies if the given location produces a collision with a block.
     *
     * @param location the location
     * @return whether a block collision is produced
     */
    boolean producesBlockCollisionAt(@NotNull Location location);
}
