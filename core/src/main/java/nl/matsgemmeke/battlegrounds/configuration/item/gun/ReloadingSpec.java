package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;

public class ReloadingSpec {

    @NotNull
    @EnumValue(type = ReloadType.class)
    public String type;

    @NotNull
    public Long duration;

    public String soundSequence;

    private enum ReloadType {
        MAGAZINE, MANUAL_INSERTION
    }
}
