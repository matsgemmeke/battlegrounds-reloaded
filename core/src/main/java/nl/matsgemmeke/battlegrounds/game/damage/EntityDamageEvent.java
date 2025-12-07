package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;

/**
 * Represents an event where an entity attacks another entity.
 *
 * @param entity  the entity being attack
 * @param damager the attacking entity
 * @param damage  the caused damage
 */
public record EntityDamageEvent(GameEntity entity, GameEntity damager, DamageNew damage) {

    public EntityDamageEvent modifyDamageAmount(double damageAmount) {
        return new EntityDamageEvent(entity, damager, new DamageNew(damageAmount, damage.type()));
    }
}
