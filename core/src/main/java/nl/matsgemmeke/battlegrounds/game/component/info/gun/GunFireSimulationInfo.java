package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GunFireSimulationInfo(List<GameSound> shotSounds, @Nullable SoundTemplate shotSoundTemplate, int rateOfFire) {
}
