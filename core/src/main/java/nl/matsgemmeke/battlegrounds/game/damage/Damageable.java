package nl.matsgemmeke.battlegrounds.game.damage;

/**
 * A game object that is able to take damage.
 */
public interface Damageable {

    double getHealth();

    void setHealth(double health);

    /**
     * Applies damage to the instance.
     *
     * @param damageAmount the amount of damage
     * @return the health after the damage was applied
     */
    double damage(double damageAmount);
}
