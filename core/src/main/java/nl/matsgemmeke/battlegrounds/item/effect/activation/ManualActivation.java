package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that initiates the effect by a manual operation by the deployer.
 */
public class ManualActivation implements ItemEffectActivation {

    @NotNull
    private Activator activator;
    private boolean primed;

    public ManualActivation(@NotNull Activator activator) {
        this.activator = activator;
        this.primed = false;
    }

    public void cancel() {
        if (!primed) {
            return;
        }

        activator.remove();
        primed = false;
    }

    public boolean isPrimed() {
        return primed;
    }

    public void prime(@NotNull ItemEffectContext context, @NotNull Procedure onActivate) {
        if (primed) {
            return;
        }

        activator.prepare(context.getDeployer());
        primed = true;
    }
}
