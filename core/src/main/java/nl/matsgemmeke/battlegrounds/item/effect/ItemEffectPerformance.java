package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;

public interface ItemEffectPerformance {

    void addTriggerRun(TriggerRun triggerRun);

    boolean isPerforming();

    void perform(ItemEffectContext context);

    void cancel();
}
