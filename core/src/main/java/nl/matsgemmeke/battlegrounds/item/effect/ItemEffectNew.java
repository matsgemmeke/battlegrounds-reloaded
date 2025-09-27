package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;

public interface ItemEffectNew {

    void addTriggerExecutor(TriggerExecutor triggerExecutor);

    ItemEffectPerformance perform(ItemEffectContext context);

    void undoPerformances();
}
