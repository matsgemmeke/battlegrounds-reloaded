package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import org.jetbrains.annotations.NotNull;

public interface ItemEffect {

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
     * Deploys an {@link ItemEffectSource} for an ongoing effect.
     *
     * @param source the source to deploy
     */
    void deploy(@NotNull ItemEffectSource source);

    /**
     * Gets whether the effect's activation system has been initiated.
     *
     * @return whether the effect is primed
     */
    boolean isPrimed();

    /**
     * Primes the effect with the provided for a specific context.
     *
     * @param context the item effect context variables
     */
    void prime(@NotNull ItemEffectContext context);

    /**
     * Undoes the performance of the effect.
     */
    default void undo() { }
}
