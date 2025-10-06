package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;

import java.util.*;

public abstract class BaseItemEffectNew implements ItemEffectNew {

    protected final List<ItemEffectPerformance> performances;
    protected final Set<TriggerExecutor> triggerExecutors;

    public BaseItemEffectNew() {
        this.performances = new ArrayList<>();
        this.triggerExecutors = new HashSet<>();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    @Override
    public Optional<ItemEffectPerformance> getLatestPerformance() {
        if (performances.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(performances.get(performances.size() - 1));
    }

    protected void startPerformance(ItemEffectPerformance performance, ItemEffectContext context) {
        if (!triggerExecutors.isEmpty()) {
            for (TriggerExecutor triggerExecutor : triggerExecutors) {
                TriggerContext triggerContext = new TriggerContext(context.getEntity(), context.getSource());

                TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
                triggerRun.addObserver(() -> performance.start(context));
                triggerRun.start();

                performance.addTriggerRun(triggerRun);
            }
        } else {
            performance.start(context);
        }

        performances.add(performance);
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
