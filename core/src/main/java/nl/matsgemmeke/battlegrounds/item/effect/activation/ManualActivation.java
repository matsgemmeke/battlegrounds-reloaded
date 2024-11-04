package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that initiates the effect by a manual operation by the item holder.
 */
public class ManualActivation extends BaseItemEffectActivation {

    @NotNull
    private Activator activator;

    public ManualActivation(@NotNull ItemEffect effect, @NotNull Activator activator) {
        super(effect);
        this.activator = activator;
    }

    public void prime(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        ItemEffectContext context = new ItemEffectContext(holder, source);
        contexts.add(context);

        activator.prepare(holder);
    }
}
