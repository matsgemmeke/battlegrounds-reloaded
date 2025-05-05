package nl.matsgemmeke.battlegrounds.item.effect.trigger.activator;

import nl.matsgemmeke.battlegrounds.item.effect.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.trigger.BaseTrigger;

import org.jetbrains.annotations.NotNull;

public class ActivatorTrigger extends BaseTrigger {

    @NotNull
    private final Activator activator;
    private boolean primed;

    public ActivatorTrigger(@NotNull Activator activator) {
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

    public void prime(@NotNull ItemEffectContext context) {
        if (primed) {
            return;
        }

        activator.prepare(context.getDeployer());
        primed = true;
    }
}
