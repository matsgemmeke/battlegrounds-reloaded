package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;

public interface Trigger {

    TriggerResult check(TriggerContext context);
}
