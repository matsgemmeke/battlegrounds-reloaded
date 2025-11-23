package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class GunFireSimulationEffectSpec extends ItemEffectSpec {

    @Required
    public Long minDuration;

    @Required
    public Long maxDuration;

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

    public String activationSounds;
}
