package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;

import java.util.Optional;

public interface ItemEffect {

    void addTriggerExecutor(TriggerExecutor triggerExecutor);

    Optional<ItemEffectPerformance> getLatestPerformance();

    void startPerformance(ItemEffectContext context);

    /**
     * Activates all current {@link ItemEffectPerformance} instances managed by this effect.
     * <p>
     * Any performances that are waiting for trigger conditions will be forced to start immediately. Performances that
     * are already active will continue as usual.
     */
    void activatePerformances();

    /**
     * Cancels all pending or scheduled {@link ItemEffectPerformance} activations.
     * <p>
     * After this call, no new performances will start until reinitialized or reactivated. Active performances will not
     * be interrupted, but those awaiting triggers will be discarded.
     */
    void cancelPerformances();

    /**
     * Reverts all side effects produced by the current {@link ItemEffectPerformance} instances.
     * <p>
     * This effectively undoes any persistent or temporary changes that were applied by the effect, restoring the
     * system to the state prior to activation.
     */
    void rollbackPerformances();
}
