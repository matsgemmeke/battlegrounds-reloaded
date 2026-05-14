package nl.matsgemmeke.battlegrounds.configuration.item.equipment.deploy;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class DropPropertiesSpec {

    @Required
    public Double velocity;

    @Required
    public Long cooldown;
}
