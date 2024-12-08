package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StickProperties(
        @NotNull List<GameSound> stickSounds,
        long checkDelay,
        long checkPeriod
) { }
