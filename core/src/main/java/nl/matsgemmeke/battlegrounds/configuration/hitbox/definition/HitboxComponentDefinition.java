package nl.matsgemmeke.battlegrounds.configuration.hitbox.definition;

import jakarta.validation.constraints.Size;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class HitboxComponentDefinition {

    @Required
    @EnumValue(type = HitboxComponentType.class)
    public String type;

    @Required
    @Size(min = 3, max = 3)
    public Double[] size;

    @Required
    @Size(min = 3, max = 3)
    public Double[] offset;

    private enum HitboxComponentType {
        HEAD, TORSO, LIMBS
    }
}
