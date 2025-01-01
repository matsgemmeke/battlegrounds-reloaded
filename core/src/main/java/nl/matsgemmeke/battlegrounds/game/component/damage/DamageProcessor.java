package nl.matsgemmeke.battlegrounds.game.component.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import nl.matsgemmeke.battlegrounds.game.damage.check.DamageCheck;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DamageProcessor {

    /**
     * Adds a {@link DamageCheck} to the DamageProcessor.
     *
     * @param damageCheck the damage check
     */
    void addDamageCheck(@NotNull DamageCheck damageCheck);

    /**
     * Determines whether damage is allowed between entities from different {@link GameContext} instances. This method
     * checks if the current {@link GameContext} can inflict or receive damage from another specified
     * {@link GameContext}.
     *
     * @param otherContext the context instance to check against the current context
     * @return {@code true} if damage is allowed from or to the context given context, {@code false} otherwise
     */
    boolean isDamageAllowed(@Nullable GameContext otherContext);

    /**
     * Process the damage produced in an event where an entity directly damages another entity.
     *
     * @param event the produced damage event
     * @return the processed damage event
     */
    @NotNull
    DamageEvent processDamage(@NotNull DamageEvent event);

    /**
     * Processes damage to be caused to a {@link DeploymentObject}.
     *
     * @param deploymentObject the deployment object to damage
     * @param damage the damage to apply
     */
    void processDeploymentObjectDamage(@NotNull DeploymentObject deploymentObject, @NotNull Damage damage);
}
