package nl.matsgemmeke.battlegrounds.configuration.hitbox.definition;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.Size;

public class HitboxComponentDefinition {

    @Required
    @EnumValue(type = HitboxComponentType.class)
    public String type;

    @Required
    @Size(exact = 3)
    public Double[] size;

    @Required
    @Size(exact = 3)
    public Double[] offset;

    private enum HitboxComponentType {
        HEAD, TORSO, LIMBS
    }
}
