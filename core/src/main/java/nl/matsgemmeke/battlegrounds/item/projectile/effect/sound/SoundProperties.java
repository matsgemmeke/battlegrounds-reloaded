package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SoundProperties(
        @NotNull List<GameSound> sounds,
        @NotNull List<Integer> intervals
) { }
