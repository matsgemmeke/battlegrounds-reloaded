package nl.matsgemmeke.battlegrounds.configuration.item.trigger;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

import java.util.List;

public class ScheduledTriggerSpec extends TriggerSpec {

    @Required
    public List<Long> offsetDelays;
}
