package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents something that acts as a source for an item effect.
 */
public interface ItemEffectSource extends Actor {

    boolean exists();

    Location getLocation();

    World getWorld();
}
