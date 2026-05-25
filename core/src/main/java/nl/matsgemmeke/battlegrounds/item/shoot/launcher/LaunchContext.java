package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.function.Supplier;

/**
 * The context in which a launch is performed.
 *
 * @param itemName              the name of the item that performs the launch
 * @param damageSource          the damage source of any damage that the launch causes
 * @param projectileSource      the projectile source from where the launch originates
 * @param direction             the launch direction
 * @param soundLocationSupplier the supplier of the location from where sounds will play
 * @param world                 the world where the launch takes place
 */
public record LaunchContext(
        String itemName,
        DamageSource damageSource,
        ProjectileLaunchSource projectileSource,
        Location direction,
        Supplier<Location> soundLocationSupplier,
        World world
) {
}
