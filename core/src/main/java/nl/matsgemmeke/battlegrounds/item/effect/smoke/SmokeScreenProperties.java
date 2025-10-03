package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;

import java.util.List;

public record SmokeScreenProperties(
        ParticleEffect particleEffect,
        List<GameSound> activationSounds,
        long minDuration,
        long maxDuration,
        double density,
        double minSize,
        double maxSize,
        double growth,
        long growthInterval
) { }
