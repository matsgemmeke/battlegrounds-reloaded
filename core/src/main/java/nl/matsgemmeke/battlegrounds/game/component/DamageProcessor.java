package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import org.jetbrains.annotations.NotNull;

public interface DamageProcessor {

    /**
     * Process the damage produced in an event where an entity directly damages another entity.
     *
     * @param event the produced damage event
     * @return the processed damage event
     */
    @NotNull
    DamageEvent processDamage(@NotNull DamageEvent event);
}
