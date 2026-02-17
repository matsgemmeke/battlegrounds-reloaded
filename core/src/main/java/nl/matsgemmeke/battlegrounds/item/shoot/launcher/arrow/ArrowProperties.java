package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;

import java.util.List;

public record ArrowProperties(List<GameSound> launchSounds, double velocity) {
}
