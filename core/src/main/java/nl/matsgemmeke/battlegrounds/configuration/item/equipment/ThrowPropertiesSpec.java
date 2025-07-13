package nl.matsgemmeke.battlegrounds.configuration.item.equipment;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ThrowPropertiesSpec {

    @Required
    public Double velocity;
    @Required
    public Long cooldown;
    public String cookSounds;
    public String throwSounds;
}
