package nl.matsgemmeke.battlegrounds.validation;

import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.HasWorld;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;
import org.bukkit.Location;
import org.bukkit.Particle;

public class TestValidationObject {

    @Required
    public String required = "test";

    @EnumValue(type = Particle.class)
    public String enumValue;

    @HasWorld
    public Location hasWorld;
}
