package nl.matsgemmeke.battlegrounds.game.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.EntityDamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;

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
     * Process the damage produced in an event where an entity directly damages another entity.
     *
     * @param event the produced damage event
     * @return the processed damage event
     */
    @NotNull
    DamageEvent processDamage(@NotNull DamageEvent event);

    EntityDamageEvent processDamage(EntityDamageEvent damageEvent);

    /**
     * Processes damage to be caused to a {@link DeploymentObject}.
     *
     * @param deploymentObject the deployment object to damage
     * @param damage the damage to apply
     */
    void processDeploymentObjectDamage(@NotNull DeploymentObject deploymentObject, @NotNull Damage damage);
}
