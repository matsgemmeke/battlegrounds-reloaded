package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GunFireSimulationInfo(List<GameSound> shotSounds, @Nullable SoundSequence shotSoundSequence, int rateOfFire) {
}
