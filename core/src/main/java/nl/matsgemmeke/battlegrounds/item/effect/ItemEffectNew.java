package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;

public interface ItemEffectNew {

    void addTriggerExecutor(TriggerExecutor triggerExecutor);

    void startPerformance(ItemEffectContext context);

    void undoPerformances();
}
