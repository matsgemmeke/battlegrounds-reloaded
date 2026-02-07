package nl.matsgemmeke.battlegrounds.item.effect;

import java.util.Optional;

public interface ItemEffect {

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
     * Reverts all side effects produced by the current {@link ItemEffectPerformance} instances.
     * <p>
     * This effectively undoes any persistent or temporary changes that were applied by the effect, restoring the
     * system to the state prior to activation.
     */
    void rollbackPerformances();
}
