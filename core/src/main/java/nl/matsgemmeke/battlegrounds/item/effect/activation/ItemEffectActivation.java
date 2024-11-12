package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
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
    void activateInstantly(@NotNull ItemHolder holder);

    /**
     * Checks whether the activation is awaiting deployment for its current process, meaning that it has primed an
     * activation process for an {@link EffectSource} which was not deployed yet. This awaiting state blocks other
     * activations until the pending deployment is finished.
     *
     * @return true if the activation is awaiting a deployment, false otherwise
     */
    boolean isAwaitingDeployment();

    /**
     * Primes the activation process with the provided {@link ItemHolder} and {@link EffectSource}.
     *
     * @param holder the holder who primes the activation
     * @param source the source from where the effect will be activated
     */
    void prime(@NotNull ItemHolder holder, @NotNull EffectSource source);
}
