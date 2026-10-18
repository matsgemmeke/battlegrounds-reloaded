package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.constraints.NotNull;

public class ThrowPropertiesSpec {

    @NotNull
    public Double velocity;

    @NotNull
    public Long cooldown;

    public String soundSequence;
}
