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
     * Checks whether the activation is currently priming its next deployment.
     *
     * @return true if the activation is currently priming, false otherwise
     */
    boolean isPrimed();

    /**
     * Primes the activation process with the provided {@link ItemHolder} and {@link EffectSource}.
     *
     * @param holder the holder who primes the activation
     * @param source the source from where the effect will be activated
     */
    void prime(@NotNull ItemHolder holder, @NotNull EffectSource source);
}
