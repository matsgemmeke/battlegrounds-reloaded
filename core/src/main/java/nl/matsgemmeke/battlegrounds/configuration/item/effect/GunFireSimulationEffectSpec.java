package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import jakarta.validation.constraints.NotNull;

public class GunFireSimulationEffectSpec extends ItemEffectSpec {

    @NotNull
    public Long minDuration;

    @NotNull
    public Long maxDuration;

    @NotNull
    public Long burstInterval;

    @NotNull
    public Long minBurstDuration;

    @NotNull
    public Long maxBurstDuration;

    @NotNull
    public Long minDelayDuration;

    @NotNull
    public Long maxDelayDuration;

    public String defaultSoundSequence;
}
