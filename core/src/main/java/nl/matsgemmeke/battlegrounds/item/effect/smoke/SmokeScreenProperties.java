package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jspecify.annotations.Nullable;

public record SmokeScreenProperties(
        ParticleEffect particleEffect,
        @Nullable
        SoundSequence activationSoundSequence,
        long minDuration,
        long maxDuration,
        double density,
        double minSize,
        double maxSize,
        double growth,
        long growthInterval
) { }
