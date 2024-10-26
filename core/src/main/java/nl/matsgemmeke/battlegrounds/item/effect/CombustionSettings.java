package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

public record CombustionSettings(
        @NotNull Iterable<GameSound> sounds,
        int radius,
        long ticksBetweenFireSpread,
        boolean burnBlocks,
        boolean spreadFire
) { }
