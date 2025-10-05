package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;

import java.util.Optional;

public interface ItemEffectNew {

    void addTriggerExecutor(TriggerExecutor triggerExecutor);

    Optional<ItemEffectPerformance> getLatestPerformance();

    void startPerformance(ItemEffectContext context);

    void undoPerformances();
}
