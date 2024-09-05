package nl.matsgemmeke.battlegrounds.game.component;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public interface DamageCalculator {

    /**
     * Calculates the amount of damage produced in an event where an entity directly damages another entity. This can
     * be, for example, a player who attack another player, or an item entity explosion inflicting damage on another
     * entity.
     *
     * @param damager the entity who inflicted damage
     * @param entity the entity who got damaged
     * @param damageCause the damage cause
     * @param damage the original damage amount
     * @return the amount of produced damage
     */
    double calculateDamage(@NotNull Entity damager, @NotNull Entity entity, @NotNull DamageCause damageCause, double damage);
}
