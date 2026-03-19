package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class HitboxMultiplierSpec {

    @Required
    public Double head;

    @Required
    public Double body;

    @Required
    public Double legs;
}
