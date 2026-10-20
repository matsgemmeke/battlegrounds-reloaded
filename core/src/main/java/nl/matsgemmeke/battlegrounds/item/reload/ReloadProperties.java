package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jetbrains.annotations.Nullable;

public record ReloadProperties(@Nullable SoundSequence soundSequence, long duration) {
}
