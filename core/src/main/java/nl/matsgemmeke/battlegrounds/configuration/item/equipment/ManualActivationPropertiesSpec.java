package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.item.validation.constraint.ValidSoundTemplate;

public class ManualActivationPropertiesSpec {

    @NotNull
    public Long delay;

    @ValidSoundTemplate
    public String activationSoundTemplate;
}
