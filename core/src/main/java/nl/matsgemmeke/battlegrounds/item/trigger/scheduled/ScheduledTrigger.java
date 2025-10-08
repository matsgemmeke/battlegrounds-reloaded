package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;

public class ScheduledTrigger implements Trigger {

    public boolean activates(TriggerContext context) {
        // Scheduled triggers have no conditions and always activate when the schedule performs its tasks
        return true;
    }
}
