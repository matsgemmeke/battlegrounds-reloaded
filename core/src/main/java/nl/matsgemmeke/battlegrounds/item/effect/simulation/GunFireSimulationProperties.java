package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GunFireSimulationProperties(
        @NotNull List<GameSound> genericSounds,
        int genericRateOfFire,
        int maxBurstDuration,
        int minBurstDuration,
        int maxDelayBetweenBursts,
        int minDelayBetweenBursts,
        int maxTotalDuration,
        int minTotalDuration
) {}
