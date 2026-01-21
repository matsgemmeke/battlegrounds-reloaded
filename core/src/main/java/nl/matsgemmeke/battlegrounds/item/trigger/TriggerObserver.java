package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;

public interface TriggerObserver {

    void onActivate(TriggerResult result);
}
