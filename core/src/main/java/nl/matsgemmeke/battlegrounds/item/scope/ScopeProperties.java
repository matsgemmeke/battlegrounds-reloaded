package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ScopeProperties(
        @NotNull List<GameSound> scopeUseSounds,
        @NotNull List<GameSound> scopeStopSounds,
        @NotNull List<Float> magnificationSettings
) { }
