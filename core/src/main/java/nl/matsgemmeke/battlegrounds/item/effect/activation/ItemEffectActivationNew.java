package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.NotNull;

public interface ItemEffectActivationNew {

    /**
     * Activates the activation process with for provided {@link ItemEffectContext}.
     *
     * @param context the item effect context
     */
    void prime(@NotNull ItemEffectContext context, @NotNull Procedure onActivate);
}
