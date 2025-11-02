package nl.matsgemmeke.battlegrounds.configuration.hitbox.definition;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class HitboxComponentDefinition {

    @Required
    @EnumValue(type = HitboxComponentType.class)
    public String type;

    @Required
    public Double[] size;

    @Required
    public Double[] offset;

    private enum HitboxComponentType {
        HEAD, TORSO, LIMBS
    }
}
