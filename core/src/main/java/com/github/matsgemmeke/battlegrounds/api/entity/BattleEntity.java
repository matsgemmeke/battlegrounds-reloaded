package com.github.matsgemmeke.battlegrounds.api.entity;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * An entity object which holds information and can perform actions in Battlegrounds.
 */
public interface BattleEntity {

    /**
     * Gets the bukkit entity object of the entity.
     *
     * @return the bukkit entity
     */
    @NotNull
    LivingEntity getEntity();

    /**
     * Applies damage to the entity.
     *
     * @param damageAmount the amount of damage
     * @return the health of the entity after the damage was applied
     */
    double damage(double damageAmount);
}
