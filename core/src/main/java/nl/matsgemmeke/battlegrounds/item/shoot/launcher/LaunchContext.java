package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.function.Supplier;

public record LaunchContext(
        DamageSource damageSource,
        ProjectileLaunchSource projectileSource,
        Location direction,
        Supplier<Location> soundLocationSupplier,
        World world
) {
}
