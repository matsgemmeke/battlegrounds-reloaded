package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;

import java.util.List;

public record CombustionProperties(
        List<GameSound> combustionSounds,
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
