package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;

import java.util.*;

public abstract class BaseItemEffect implements ItemEffect {

    protected final List<ItemEffectPerformance> performances;
    protected final Set<TriggerExecutor> triggerExecutors;
    protected ItemEffectContext context;

    public BaseItemEffect() {
        this.performances = new ArrayList<>();
        this.triggerExecutors = new HashSet<>();
    }

    @Override
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
        performances.add(performance);

        performance.setContext(context);

        if (!triggerExecutors.isEmpty()) {
            UUID uniqueId = context.getDamageSource().getUniqueId();
            ItemEffectSource effectSource = context.getEffectSource();

            for (TriggerExecutor triggerExecutor : triggerExecutors) {
                TriggerContext triggerContext = new TriggerContext(uniqueId, effectSource);

                TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
                triggerRun.addObserver(performance::start);
                triggerRun.start();

                performance.addTriggerRun(triggerRun);
            }
        } else {
            performance.start();
        }
    }

    @Override
    public void activatePerformances() {
        for (ItemEffectPerformance performance : performances) {
            if (!performance.isPerforming()) {
                performance.cancel();
                performance.start();
            }
        }

        performances.clear();
    }

    @Override
    public void cancelPerformances() {
        for (ItemEffectPerformance performance : performances) {
            if (!performance.isPerforming()) {
                performance.cancel();
            }
        }

        performances.clear();
    }

    @Override
    public void rollbackPerformances() {
        for (ItemEffectPerformance performance : performances) {
            if (performance.isPerforming()) {
                performance.rollback();
            }
        }

        performances.clear();
    }
}
