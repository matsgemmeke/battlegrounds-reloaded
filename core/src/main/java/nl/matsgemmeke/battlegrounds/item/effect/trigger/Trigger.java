package nl.matsgemmeke.battlegrounds.item.effect.trigger;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.NotNull;

public interface Trigger {

    void addObserver(@NotNull TriggerObserver observer);

    /**
     * Cancels the trigger.
     */
    void cancel();

    /**
     * Gets whether the trigger system is primed.
     *
     * @return whether the trigger is primed
     */
    boolean isPrimed();

    /**
     * Activates the trigger process for the provided {@link ItemEffectContext}.
     *
     * @param context the item effect context
     */
    void prime(@NotNull ItemEffectContext context);
}
