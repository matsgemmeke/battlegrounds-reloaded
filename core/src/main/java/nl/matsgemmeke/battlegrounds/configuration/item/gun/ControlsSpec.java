package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ControlsSpec {

    @Required
    public String reload;
    @Required
    public String shoot;
    public String scopeUse;
    public String scopeStop;
    public String changeScopeMagnification;
}
