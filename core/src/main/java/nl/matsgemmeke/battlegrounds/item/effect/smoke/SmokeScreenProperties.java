package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.ParticleEffectProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SmokeScreenProperties(
        @NotNull ParticleEffectProperties particleEffect,
        @NotNull List<GameSound> ignitionSounds,
        int duration,
        double density,
        double radiusMaxSize,
        double radiusStartingSize,
        double growthIncrease,
        long growthPeriod
) { }
