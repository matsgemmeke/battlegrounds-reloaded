package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseItemEffectNew implements ItemEffectNew {

    @NotNull
    protected final Set<ItemEffectPerformance> performances;
    @NotNull
    protected final Set<TriggerExecutor> triggerExecutors;

    public BaseItemEffectNew() {
        this.performances = new HashSet<>();
        this.triggerExecutors = new HashSet<>();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    public void undoPerformances() {
        for (ItemEffectPerformance performance : performances) {
            if (performance.isPerforming()) {
                performance.cancel();
            }
        }

        performances.clear();
    }
}
