package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Activation that initiates the effect based on an external trigger.
 */
public class TriggerActivation implements ItemEffectActivation {

    private boolean primed;
    @NotNull
    private List<Trigger> triggers;

    public TriggerActivation() {
        this.triggers = new ArrayList<>();
        this.primed = false;
    }

    public void addTrigger(@NotNull Trigger trigger) {
        triggers.add(trigger);
    }

    public void cancel() {
        if (!primed) {
            return;
        }

        primed = false;

        for (Trigger trigger : triggers) {
            trigger.cancel();
        }
    }

    public boolean isPrimed() {
        return primed;
    }

    public void prime(@NotNull ItemEffectContext context, @NotNull Procedure onActivate) {
        if (primed) {
            return;
        }

        primed = true;

        ItemHolder holder = context.getHolder();
        ItemEffectSource source = context.getSource();

        if (source.isDeployed()) {
            holder.setHeldItem(null);
        }

        for (Trigger trigger : triggers) {
            trigger.addObserver(onActivate::apply);
            trigger.checkTriggerActivation(context);
        }
    }
}
