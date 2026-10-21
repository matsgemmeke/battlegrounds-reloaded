package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record ScopeProperties(
        List<Float> magnifications,
        @Nullable
        SoundSequence useSoundSequence,
        @Nullable
        SoundSequence stopSoundSequence,
        @Nullable
        SoundSequence changeMagnificationSoundSequence
) { }
