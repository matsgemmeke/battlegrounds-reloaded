package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.game.damage.Damageable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * An entity object which holds information and can perform actions in Battlegrounds.
 */
public interface GameEntity extends Damageable, Identifiable {

    /**
     * Gets the bukkit entity object of the entity.
     *
     * @return the bukkit entity
     */
    @NotNull
    LivingEntity getEntity();

    /**
     * Gets the location of the entity.
     *
     * @return the entity location
     */
    @NotNull
    Location getLocation();

    /**
     * Gets the name of the entity.
     *
     * @return the entity name
     */
    @NotNull
    String getName();

    /**
     * Gets the world the entity is located in.
     *
     * @return the entity's world
     */
    @NotNull
    World getWorld();
}
