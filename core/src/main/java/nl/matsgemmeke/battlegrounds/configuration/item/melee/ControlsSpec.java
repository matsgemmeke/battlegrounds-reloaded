package nl.matsgemmeke.battlegrounds.configuration.item.melee;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;

public class ControlsSpec {

    @Valid
    public ControlSpec reload;

    @Valid
    public ControlSpec throwing;
}
