package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import jakarta.validation.Valid;
import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class ControlsSpec {

    @Required
    @Valid
    public ControlSpec reload;

    @Required
    @Valid
    public ControlSpec shoot;

    @Valid
    public ControlSpec scopeUse;

    @Valid
    public ControlSpec scopeStop;

    @Valid
    public ControlSpec scopeChangeMagnification;
}
