package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.NotNull;

public interface ItemEffectActivation {

    /**
     * Cancels the effect activation.
     */
    void cancel();

    /**
     * Gets whether the effect activation system is primed.
     *
     * @return whether the activation is primed
     */
    boolean isPrimed();

    /**
     * Activates the activation process with for provided {@link ItemEffectContext}.
     *
     * @param context the item effect context
     */
    void prime(@NotNull ItemEffectContext context, @NotNull Procedure onActivate);
}
