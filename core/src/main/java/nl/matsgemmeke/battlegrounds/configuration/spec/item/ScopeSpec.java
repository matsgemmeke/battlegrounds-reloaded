package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import java.util.List;

public record ScopeSpec(
        List<Float> magnifications,
        String useSounds,
        String stopSounds,
        String changeMagnificationSounds
) { }
