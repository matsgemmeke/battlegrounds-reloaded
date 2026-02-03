package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.Target;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * An entity object which holds information and can perform actions in Battlegrounds.
 */
public interface GameEntity extends Target, DamageTarget {

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

    /**
     * Gets the unique id of the game entity. Usually this will return the unique id of the corresponding entity.
     * Otherwise, a separate id will be used.
     *
     * @return the game entity's unique id
     */
    UUID getUniqueId();

    /**
     * Gets this entity's current velocity
     *
     * @return the current traveling velocity of the entity
     */
    Vector getVelocity();

    /**
     * Gets the world the game entity is located in.
     *
     * @return the game entity world
     */
    World getWorld();

    /**
     * Returns whether the entity has died or been despawned for some other reason.
     *
     * @return whether the entity is valid
     */
    boolean isValid();
}
