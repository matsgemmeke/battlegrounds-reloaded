package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ScopeProperties(
        @NotNull List<Float> magnifications,
        @NotNull List<GameSound> useSounds,
        @NotNull List<GameSound> stopSounds,
        @NotNull List<GameSound> changeMagnificationSounds
) { }
