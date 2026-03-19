package nl.matsgemmeke.battlegrounds.validation.validator;

import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;
import org.bukkit.Particle;

public class TestValidationObject {

    @Required
    public String required = "test";

    @EnumValue(type = Particle.class)
    public String enumValue;
}
