package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jspecify.annotations.Nullable;

public record ItemLaunchProperties(
        ItemTemplate itemTemplate,
        @Nullable
        SoundSequence soundSequence,
        double velocity,
        int pickupDelay
) {
}
