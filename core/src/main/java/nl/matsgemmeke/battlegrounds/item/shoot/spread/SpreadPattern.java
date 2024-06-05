package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the different directions for projectiles that consist of multiple parts.
 */
public interface SpreadPattern {

    /**
     * Gets all directions the pattern would shoot a projectile to for one single shot.
     *
     * @param aimDirection the original aiming direction
     * @return an iterable containing locations as projectile directions
     */
    @NotNull
    Iterable<Location> getProjectileDirections(@NotNull Location aimDirection);
}
