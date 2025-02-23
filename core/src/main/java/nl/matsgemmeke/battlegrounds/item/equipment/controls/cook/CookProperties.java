package nl.matsgemmeke.battlegrounds.item.equipment.controls.cook;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

public record CookProperties(
        @NotNull Iterable<GameSound> cookSounds
) { }
