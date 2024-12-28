package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that initiates the effect by a manual operation by the item holder.
 */
public class ManualActivation implements ItemEffectActivationNew {

    @NotNull
    private Activator activator;

    public ManualActivation(@NotNull Activator activator) {
        this.activator = activator;
    }

    public void prime(@NotNull ItemEffectContext context, @NotNull Procedure onActivate) {
        activator.prepare(context.getHolder());
    }
}
