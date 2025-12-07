package nl.matsgemmeke.battlegrounds.game.component.damage;

import org.bukkit.entity.Entity;

/**
 * Provides methods for processing damage that originates through event handlers.
 */
public interface EventDamageAdapter {

    /**
     * Processes damages caused by a melee attack between two entities
     *
     * @param victim       the damaged entity
     * @param damager      the damaging entity
     * @param damageAmount the amount of caused damage
     * @return             the produced event result
     */
    EventDamageResult processMeleeDamage(Entity victim, Entity damager, double damageAmount);
}
