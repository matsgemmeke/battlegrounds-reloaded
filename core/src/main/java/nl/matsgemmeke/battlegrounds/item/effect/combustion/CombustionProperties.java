package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CombustionProperties(
        @NotNull List<GameSound> combustionSounds,
        int maxRadius,
        long ticksBetweenFireSpread,
        long maxDuration,
        boolean burnBlocks,
        boolean spreadFire
) {}
