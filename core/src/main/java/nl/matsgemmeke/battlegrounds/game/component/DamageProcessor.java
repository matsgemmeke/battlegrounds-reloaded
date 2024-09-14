package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DamageProcessor {

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
}
