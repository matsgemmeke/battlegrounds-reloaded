package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseItemEffectPerformance implements ItemEffectPerformance {

    protected final Set<TriggerRun> triggerRuns;
    protected ItemEffectContext context;

    public BaseItemEffectPerformance() {
        this.triggerRuns = new HashSet<>();
    }

    @Override
    public void addTriggerRun(TriggerRun triggerRun) {
        triggerRuns.add(triggerRun);
    }

    @Override
    public void changeSource(ItemEffectSource source) {
        context.setSource(source);
    }

    @Override
    public boolean isReleased() {
        return context != null && context.getSource().isReleased();
    }

    @Override
    public void setContext(ItemEffectContext context) {
        this.context = context;
    }

    @Override
    public void start() {
        this.perform(context);
    }

    public abstract void perform(ItemEffectContext context);

    @Override
    public void cancel() {
        triggerRuns.forEach(TriggerRun::cancel);
        triggerRuns.clear();
    }

    /**
     * By default, this method is a no-op, meaning the effect performance has no side effects. Override this method to
     * implement specific rollback logic for the effect performance implementation.
     */
    @Override
    public void rollback() {
    }
}
