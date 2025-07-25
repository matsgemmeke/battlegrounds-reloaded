package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import org.jetbrains.annotations.NotNull;

public interface Effect {

    /**
     * Activates the effect instantly, overriding any other procedures.
     */
    void activateInstantly();

    /**
     * Adds a trigger to the item effect.
     *
     * @param trigger the trigger instance
     */
    void addTrigger(@NotNull Trigger trigger);

    /**
     * Cancels the current activation process of the effect. This method does not do anything if the effect was already
     * activated.
     */
    void cancelActivation();

    /**
     * Deploys an {@link EffectSource} for an ongoing effect.
     *
     * @param source the source to deploy
     */
    void deploy(@NotNull EffectSource source);

    /**
     * Gets whether the effect's activation system has been initiated.
     *
     * @return whether the effect is primed
     */
    boolean isPrimed();

    /**
     * Primes the effect with the provided for a specific {@link EffectContext}.
     *
     * @param context the item effect context
     */
    void prime(@NotNull EffectContext context);

    /**
     * Undoes the performance of the effect.
     */
    default void undo() { }
}
