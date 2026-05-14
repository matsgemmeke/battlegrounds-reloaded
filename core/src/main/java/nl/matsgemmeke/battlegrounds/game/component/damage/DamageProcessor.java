package nl.matsgemmeke.battlegrounds.game.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;

public interface DamageProcessor {

    /**
     * Adds a {@link DamageModifier} to the damage processor.
     *
     * @param modifier the damage modifier
     */
    void addDamageModifier(DamageModifier modifier);

    /**
     * Determines whether damage is allowed between entities from different game contexts. This method checks if the
     * instance's corresponding game can inflict or receive damage from another specified game.
     *
     * @param gameKey the game key to check with the corresponding game
     * @return {@code true} if damage is allowed from or to the given game, {@code false} otherwise
     */
    boolean isDamageAllowed(GameKey gameKey);

    /**
     * Determines whether damage is allowed between entities that are not located inside a game context.
     *
     * @return {@code true} if damage is allowed from or to the given game, {@code false} otherwise
     */
    boolean isDamageAllowedWithoutContext();

    /**
     * Process the damage produced in the context of a damage event.
     *
     * @param damageContext the damage context
     */
    void processDamage(DamageContext damageContext);
}
