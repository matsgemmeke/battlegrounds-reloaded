package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class ThrowPropertiesSpec {

    @Required
    public Double velocity;

    @Required
    public Long cooldown;

    public String throwSounds;
}
