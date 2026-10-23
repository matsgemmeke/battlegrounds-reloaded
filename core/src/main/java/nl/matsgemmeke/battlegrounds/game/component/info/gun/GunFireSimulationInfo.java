package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jetbrains.annotations.Nullable;

public record GunFireSimulationInfo(@Nullable SoundSequence shotSoundSequence, int rateOfFire) {
}
