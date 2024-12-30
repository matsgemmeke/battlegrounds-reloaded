package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;

public interface ItemEffect {

    /**
     * Activates the effect instantly, overriding any other procedures.
     */
    void activateInstantly();

    /**
     * Deploys a {@link EffectSource} for an ongoing effect.
     *
     * @param source the source to deploy
     */
    void deploy(@NotNull EffectSource source);

    /**
     * Checks whether the effect is awaiting deployment for its current process, meaning that it has primed an
     * activation process for an {@link EffectSource} which was not deployed yet. This awaiting state blocks other
     * activations until the pending deployment is finished.
     *
     * @return true if the effect is awaiting a deployment, false otherwise
     */
    boolean isAwaitingDeployment();

    /**
     * Gets whether the effect's activation system has been initiated.
     *
     * @return whether the effect is primed
     */
    boolean isPrimed();

    /**
     * Primes the effect with the provided for a specific {@link ItemEffectContext}.
     *
     * @param context the item effect context
     */
    void prime(@NotNull ItemEffectContext context);

    /**
     * Resets the performance of the effect. If the effect is still in activation phase the activation will be
     * cancelled.
     */
    default void reset() { }
}
