package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseItemEffect implements ItemEffectNew {

    private boolean primed;
    @NotNull
    protected ItemEffectActivation effectActivation;
    @Nullable
    protected ItemEffectContext currentContext;

    public BaseItemEffect(@NotNull ItemEffectActivation effectActivation) {
        this.effectActivation = effectActivation;
        this.primed = false;
    }

    public void activateInstantly() {
        if (currentContext != null) {
            this.perform(currentContext);
        }
    }

    public void deploy(@NotNull EffectSource source) {
        if (currentContext == null) {
            return;
        }

        currentContext.setSource(source);
    }

    public boolean isAwaitingDeployment() {
        return primed && currentContext != null && !currentContext.getSource().isDeployed();
    }

    public boolean isPrimed() {
        return primed;
    }

    public void prime(@NotNull ItemEffectContext context) {
        if (primed) {
            return;
        }

        currentContext = context;
        primed = true;

        effectActivation.prime(context, () -> this.perform(currentContext));
    }

    public abstract void perform(@NotNull ItemEffectContext context);
}
