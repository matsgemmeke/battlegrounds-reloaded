package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;

import java.util.List;

public record GunFireSimulationProperties(
        List<GameSound> genericSounds,
        long burstInterval,
        long minBurstDuration,
        long maxBurstDuration,
        long minDelayDuration,
        long maxDelayDuration,
        long minTotalDuration,
        long maxTotalDuration
) {}
