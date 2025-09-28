package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;

public interface ItemEffectNew {

    void addTriggerExecutor(TriggerExecutor triggerExecutor);

    ItemEffectPerformance start(ItemEffectContext context);

    void undoPerformances();
}
