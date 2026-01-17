package nl.matsgemmeke.battlegrounds.item.trigger;

import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;

public interface Trigger {

    boolean activates(TriggerContext context);

    TriggerResult check(TriggerContext context);
}
