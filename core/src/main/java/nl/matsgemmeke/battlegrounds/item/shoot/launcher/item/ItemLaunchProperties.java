package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;

import java.util.List;

public record ItemLaunchProperties(ItemTemplate itemTemplate, List<GameSound> launchSounds, double velocity, int pickupDelay) {
}
