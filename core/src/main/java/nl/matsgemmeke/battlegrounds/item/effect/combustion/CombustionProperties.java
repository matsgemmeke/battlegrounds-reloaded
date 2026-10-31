package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jspecify.annotations.Nullable;

public record CombustionProperties(
        @Nullable
        SoundSequence soundSequence,
        RangeProfile rangeProfile,
        double minSize,
        double maxSize,
        double growth,
        long growthInterval,
        long minDuration,
        long maxDuration,
        boolean burnBlocks,
        boolean spreadFire
) { }
