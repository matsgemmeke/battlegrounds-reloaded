package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.effect.trigger.Trigger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseItemEffect implements ItemEffect {

    private boolean activated;
    private boolean primed;
    @Nullable
    protected ItemEffectContext currentContext;
    @NotNull
    protected final List<Trigger> triggers;

    public BaseItemEffect() {
        this.activated = false;
        this.primed = false;
        this.triggers = new ArrayList<>();
    }

    public void activateInstantly() {
        if (activated || currentContext == null) {
            return;
        }

        activated = true;
        triggers.forEach(Trigger::cancel);
        this.perform(currentContext);
    }

    public void addTrigger(@NotNull Trigger trigger) {
        triggers.add(trigger);
    }

    public void cancelActivation() {
        if (!primed || activated) {
            return;
        }

        triggers.forEach(Trigger::cancel);
    }

    public void deploy(@NotNull ItemEffectSource source) {
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

        triggers.forEach(trigger -> {
            trigger.addObserver(() -> {
                activated = true;
                this.perform(currentContext);
            });
            trigger.prime(context);
        });
    }

    public abstract void perform(@NotNull ItemEffectContext context);
}
