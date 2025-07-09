package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ManualActivationPropertiesSpec {

    @Required
    public Long delay;
    public String activationSounds;
}
