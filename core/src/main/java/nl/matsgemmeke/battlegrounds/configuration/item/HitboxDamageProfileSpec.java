package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class HitboxDamageProfileSpec {

    @Required
    public Double head;

    @Required
    public Double torso;

    @Required
    public Double limbs;
}
