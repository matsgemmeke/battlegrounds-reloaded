package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * An entity object which holds information and can perform actions in Battlegrounds.
 */
public interface GameEntity {

    /**
     * Gets the bukkit entity object of the entity.
     *
     * @return the bukkit entity
     */
    @NotNull
    Entity getEntity();

    /**
     * Applies damage to the entity.
     *
     * @param damageAmount the amount of damage
     * @return the health of the entity after the damage was applied
     */
    double damage(double damageAmount);
}
