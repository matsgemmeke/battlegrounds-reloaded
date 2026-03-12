package nl.matsgemmeke.battlegrounds.validation.common.validator;

import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;
import org.bukkit.Particle;

public class TestValidationObject {

    @Required
    public String required = "test";

    @EnumValue(type = HitboxComponentType.class)
    public String enumValueSmallEnum;

    @EnumValue(type = Particle.class)
    public String enumValueLargeEnum;
}
