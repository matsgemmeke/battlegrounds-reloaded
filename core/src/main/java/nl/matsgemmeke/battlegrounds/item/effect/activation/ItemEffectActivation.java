package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for objects that control the activation of an {@link ItemEffect}.
 */
public interface ItemEffectActivation {

    /**
     * Activates the effect instantly, overriding any other procedures.
     *
     * @param holder the holder who triggers the activation
     */
    void activateDeployedObjects(@NotNull ItemHolder holder);

    /**
     * Notifies the effect activation that a {@link Deployable} object was deployed whose activation had already
     * begun.
     *
     * @param object the deferred deployable object
     */
    void deploy(@NotNull Deployable object);

    /**
     * Checks whether the activation is currently priming its next deployment.
     *
     * @return true if the activation is currently priming, false otherwise
     */
    boolean isPrimed();

    /**
     * Prepares and deploys a {@link Deployable} object.
     *
     * @param holder the holder who deploys the object
     * @param object the deployable object used in the priming process
     */
    void primeDeployedObject(@NotNull ItemHolder holder, @NotNull Deployable object);

    /**
     * Prepares the activation in the hand of an {@link ItemHolder} in case a {@link Deployable} object is to be thrown
     * at a later moment.
     *
     * @param holder the holder who primes the activation
     * @param itemStack the item stack used to prime the activation
     */
    void primeInHand(@NotNull ItemHolder holder, @NotNull ItemStack itemStack);
}
