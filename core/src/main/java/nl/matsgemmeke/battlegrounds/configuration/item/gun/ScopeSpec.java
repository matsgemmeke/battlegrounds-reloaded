package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ScopeSpec {

    @NotNull
    @Size(min = 1)
    public Float[] magnifications;

    public String useSoundSequence;

    public String stopSoundSequence;

    public String changeMagnificationSoundSequence;
}
