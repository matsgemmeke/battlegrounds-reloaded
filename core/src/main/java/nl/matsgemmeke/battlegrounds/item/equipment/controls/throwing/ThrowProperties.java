package nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

public record ThrowProperties(
        @NotNull Iterable<GameSound> throwSounds,
        double velocity,
        long delayAfterThrow
) { }
