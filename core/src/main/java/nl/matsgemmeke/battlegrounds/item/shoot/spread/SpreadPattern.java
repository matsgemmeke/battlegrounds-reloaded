package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Defines the different directions for projectiles that consist of multiple parts.
 */
public interface SpreadPattern {

    /**
     * Gets all directions the pattern would shoot a projectile to for one single shot.
     *
     * @param shootingDirection the original shooting direction
     * @return an iterable containing locations as projectile directions
     */
    @NotNull
    List<Location> getShootingDirections(@NotNull Location shootingDirection);
}
