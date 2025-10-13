package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;

import java.util.List;

public record GunFireSimulationInfo(List<GameSound> shotSounds, int rateOfFire) {
}
