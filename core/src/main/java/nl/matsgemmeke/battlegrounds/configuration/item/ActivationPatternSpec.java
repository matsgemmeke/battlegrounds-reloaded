package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ActivationPatternSpec {

    @Required
    public Long burstInterval;
    @Required
    public Long minBurstDuration;
    @Required
    public Long maxBurstDuration;
    @Required
    public Long minDelayDuration;
    @Required
    public Long maxDelayDuration;
}
