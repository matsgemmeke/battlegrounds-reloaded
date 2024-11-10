package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

public record SmokeScreenSettings(
        @NotNull Iterable<GameSound> ignitionSounds,
        int duration,
        double density,
        double radiusMaxSize,
        double radiusStartingSize,
        double growthIncrease,
        long growthPeriod
) { }
