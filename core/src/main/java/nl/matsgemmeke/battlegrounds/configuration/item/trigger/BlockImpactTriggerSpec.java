package nl.matsgemmeke.battlegrounds.configuration.item.trigger;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class BlockImpactTriggerSpec extends TriggerSpec {

    @Required
    public Long delay;

    @Required
    public Long interval;
}
