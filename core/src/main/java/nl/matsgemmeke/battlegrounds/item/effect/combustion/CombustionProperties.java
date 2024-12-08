package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

public record CombustionProperties(
        @NotNull Iterable<GameSound> combustionSounds,
        int radius,
        long ticksBetweenFireSpread,
        boolean burnBlocks,
        boolean spreadFire
) {}
