package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.result.SimpleTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;

public class ScheduledTrigger implements Trigger {

    public boolean activates(TriggerContext context) {
        return true;
    }

    @Override
    public TriggerResult check(TriggerContext context) {
        // Scheduled triggers have no conditions and always activate when the schedule performs its tasks
        return SimpleTriggerResult.ACTIVATES;
    }
}
