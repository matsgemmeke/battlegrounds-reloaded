package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.jspecify.annotations.Nullable;

public record ArrowProperties(@Nullable SoundTemplate launchSoundTemplate, double velocity) {
}
