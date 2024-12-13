package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GunFireSimulationInfo(
        @NotNull List<GameSound> shotSounds,
        int rateOfFire
) {}
