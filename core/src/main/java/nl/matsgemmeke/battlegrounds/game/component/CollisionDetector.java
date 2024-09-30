package nl.matsgemmeke.battlegrounds.game.component;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A façade interface whose responsibility is to detect possible collision in a game context.
 */
public interface CollisionDetector {

    /**
     * Verifies if the given location produces a collision with a block.
     *
     * @param location the location
     * @return whether a block collision is produced
     */
    boolean producesBlockCollisionAt(@NotNull Location location);
}
