package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class ManualActivationPropertiesSpec {

    @Required
    public Long delay;

    public String activationSounds;
}
