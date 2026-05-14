package nl.matsgemmeke.battlegrounds.configuration.item.trigger;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class FloorHitTriggerSpec extends TriggerSpec {

    @Required
    public Long delay;

    @Required
    public Long interval;
}
