package nl.matsgemmeke.battlegrounds.item.mechanism.smoke;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

public record SmokeScreenSettings(
        @NotNull Iterable<GameSound> ignitionSounds,
        int size
) { }
