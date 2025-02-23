package nl.matsgemmeke.battlegrounds.item.equipment.controls.place;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public record PlaceProperties(
        @NotNull Iterable<GameSound> placeSounds,
        Material material,
        long delayAfterPlacement
) { }
