package nl.matsgemmeke.battlegrounds.item.trigger.scheduled;

import nl.matsgemmeke.battlegrounds.item.trigger.CheckResult;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;

import java.util.Optional;

public class ScheduledTrigger implements Trigger {

    public boolean activates(TriggerContext context) {
        // Scheduled triggers have no conditions and always activate when the schedule performs its tasks
        return true;
    }

    @Override
    public Optional<CheckResult> check(TriggerContext context) {
        return Optional.empty();
    }
}
