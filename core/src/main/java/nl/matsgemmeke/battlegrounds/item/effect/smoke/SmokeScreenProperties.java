package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SmokeScreenProperties(
        @NotNull ParticleEffect particleEffect,
        @NotNull List<GameSound> activationSounds,
        long minDuration,
        long maxDuration,
        double density,
        double minSize,
        double maxSize,
        double growth,
        long growthInterval
) { }
