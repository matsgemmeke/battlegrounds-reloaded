package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.constraints.NotNull;

public class ManualActivationPropertiesSpec {

    @NotNull
    public Long delay;

    public String activationSoundSequence;
}
