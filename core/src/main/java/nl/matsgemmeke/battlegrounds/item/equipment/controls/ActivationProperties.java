package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

public record ActivationProperties(
        @NotNull Iterable<GameSound> activationSounds,
        long delayUntilActivation
) { }
