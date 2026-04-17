package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;

public class ControlsSpec {

    @Valid
    public ControlSpec throwing;

    @Valid
    public ControlSpec cook;

    @Valid
    public ControlSpec drop;

    @Valid
    public ControlSpec place;

    @Valid
    public ControlSpec activate;
}
