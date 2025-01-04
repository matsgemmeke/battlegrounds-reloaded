package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseItemEffect implements ItemEffect {

    private boolean activated;
    private boolean primed;
    @NotNull
    protected ItemEffectActivation effectActivation;
    @Nullable
    protected ItemEffectContext currentContext;

    public BaseItemEffect(@NotNull ItemEffectActivation effectActivation) {
        this.effectActivation = effectActivation;
        this.activated = false;
        this.primed = false;
    }

    public void activateInstantly() {
        if (activated || currentContext == null) {
            return;
        }

        activated = true;
        effectActivation.cancel();
        this.perform(currentContext);
    }

    public void cancelActivation() {
        if (!primed || activated) {
            return;
        }

        effectActivation.cancel();
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

        effectActivation.prime(context, () -> {
            activated = true;
            this.perform(currentContext);
        });
    }

    public abstract void perform(@NotNull ItemEffectContext context);
}
