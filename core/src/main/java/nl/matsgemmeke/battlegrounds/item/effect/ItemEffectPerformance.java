package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;

public interface ItemEffectPerformance {

    void addTriggerRun(TriggerRun triggerRun);

    void changeSource(ItemEffectSource source);

    boolean isPerforming();

    boolean isReleased();

    void start(ItemEffectContext context);

    void cancel();

    void rollback();
}
