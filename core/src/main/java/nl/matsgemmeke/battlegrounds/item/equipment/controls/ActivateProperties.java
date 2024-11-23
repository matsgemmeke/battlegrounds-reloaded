package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

public record ActivateProperties(
        @NotNull Iterable<GameSound> activationSounds,
        long delayUntilActivation
) { }
