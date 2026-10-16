package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.item.validation.constraint.ValidSoundTemplate;

public class ThrowPropertiesSpec {

    @NotNull
    public Double velocity;

    @NotNull
    public Long cooldown;

    @ValidSoundTemplate
    public String soundTemplate;
}
