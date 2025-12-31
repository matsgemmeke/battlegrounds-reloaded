package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents something that acts as a source for an item effect.
 */
public interface ItemEffectSource {

    boolean exists();

    Location getLocation();

    World getWorld();
}
