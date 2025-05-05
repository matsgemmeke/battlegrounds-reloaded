package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CombustionProperties(
        @NotNull List<GameSound> combustionSounds,
        double minSize,
        double maxSize,
        double growth,
        long growthInterval,
        long minDuration,
        long maxDuration,
        boolean burnBlocks,
        boolean spreadFire
) { }
