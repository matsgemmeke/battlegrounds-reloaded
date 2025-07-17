package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEffect implements Effect {

    private boolean activated;
    private boolean primed;
    @Nullable
    protected EffectContext currentContext;
    @NotNull
    protected final List<Trigger> triggers;

    public BaseEffect() {
        this.activated = false;
        this.primed = false;
        this.triggers = new ArrayList<>();
    }

    public void activateInstantly() {
        if (activated || currentContext == null) {
            return;
        }

        activated = true;
        triggers.forEach(Trigger::stop);
        this.perform(currentContext);
    }

    public void addTrigger(@NotNull Trigger trigger) {
        triggers.add(trigger);
    }

    public void cancelActivation() {
        if (!primed || activated) {
            return;
        }

        triggers.forEach(Trigger::stop);
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

    public void prime(@NotNull EffectContext context) {
        if (primed) {
            return;
        }

        currentContext = context;
        primed = true;

        for (Trigger trigger : triggers) {
            TriggerContext triggerContext = new TriggerContext(currentContext.getEntity(), currentContext.getSource());

            trigger.addObserver(() -> {
                activated = true;
                this.perform(currentContext);
            });
            trigger.start(triggerContext);
        }
    }

    public abstract void perform(@NotNull EffectContext context);
}
