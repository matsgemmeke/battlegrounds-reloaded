package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jspecify.annotations.Nullable;

public record GunFireSimulationProperties(
        @Nullable
        SoundSequence defaultSoundSequence,
        long burstInterval,
        long minBurstDuration,
        long maxBurstDuration,
        long minDelayDuration,
        long maxDelayDuration,
        long minTotalDuration,
        long maxTotalDuration
) {}
