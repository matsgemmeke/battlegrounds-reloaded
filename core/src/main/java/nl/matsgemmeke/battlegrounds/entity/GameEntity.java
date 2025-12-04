package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.game.damage.Target;
import org.bukkit.Location;

/**
 * An entity object which holds information and can perform actions in Battlegrounds.
 */
public interface GameEntity extends Target, Identifiable {

    /**
     * Gets the hitbox of the entity.
     *
     * @return the entity hitbox
     */
    Hitbox getHitbox();

    /**
     * Gets the location of the entity.
     *
     * @return the entity location
     */
    Location getLocation();

    /**
     * Gets the name of the entity.
     *
     * @return the entity name
     */
    String getName();
}
