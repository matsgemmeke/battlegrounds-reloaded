package nl.matsgemmeke.battlegrounds.game.damage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A game object that is able to take damage.
 */
public interface Damageable {

    double getHealth();

    void setHealth(double health);

    @Nullable
    Damage getLastDamage();

    /**
     * Applies damage to the instance.
     *
     * @param damage the damage object
     * @return the actual damage amount that was dealt
     */
    double damage(@NotNull Damage damage);

    /**
     * Gets whether the object is completely immune to a specific type of damage.
     *
     * @param damageType the type of damage
     * @return whether the object is immune to the given damage type
     */
    boolean isImmuneTo(@NotNull DamageType damageType);
}
