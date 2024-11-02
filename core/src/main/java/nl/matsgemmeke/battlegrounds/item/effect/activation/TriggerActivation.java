package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Activation that initiates the effect based on an external trigger.
 */
public class TriggerActivation extends BaseItemEffectActivation {

    @NotNull
    private List<Trigger> triggers;

    public TriggerActivation(@NotNull ItemEffect effect) {
        super(effect);
        this.triggers = new ArrayList<>();
    }

    public void addTrigger(@NotNull Trigger trigger) {
        triggers.add(trigger);

        trigger.addObserver(effect::activate);
    }

    public void prime(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        sources.add(source);

        if (source.isDeployed()) {
            holder.setHeldItem(null);
        }

        for (Trigger trigger : triggers) {
            trigger.checkTriggerActivation(holder, source);
        }
    }
}
