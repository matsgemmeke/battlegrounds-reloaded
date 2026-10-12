package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.jspecify.annotations.Nullable;

public record ItemLaunchProperties(
        ItemTemplate itemTemplate,
        @Nullable
        SoundTemplate launchSoundTemplate,
        double velocity,
        int pickupDelay
) {
}
