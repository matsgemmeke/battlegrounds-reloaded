package nl.matsgemmeke.battlegrounds.game.damage;

import org.bukkit.entity.Entity;

/**
 * Represents an event where an entity attacks another entity.
 *
 * @param entity  the entity being attack
 * @param damager the attacking entity
 * @param damage  the caused damage
 */
public record DamageEventNew(Entity entity, Entity damager, DamageNew damage) {

    public DamageEventNew modifyDamageAmount(double damageAmount) {
        return new DamageEventNew(entity, damager, new DamageNew(damageAmount, damage.type()));
    }
}
