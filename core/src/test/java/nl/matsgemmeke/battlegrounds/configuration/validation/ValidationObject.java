package nl.matsgemmeke.battlegrounds.configuration.validation;

import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.EnumValues;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.Size;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import org.bukkit.Particle;

import java.util.List;

public class ValidationObject {

    @Required
    public String required;

    @EnumValue(type = Particle.class)
    public String enumValue;

    @EnumValues(type = HitboxComponentType.class)
    public List<String> enumValues;

    @EnumValues(type = Particle.class)
    public List<String> enumValuesLargeEnum;

    @EnumValues(type = Particle.class)
    public String enumValuesInvalidType;

    @Regex(pattern = "^[a-c]{3}")
    public String regex;

    @Size(exact = 1)
    public String sizeWithExact;

    @Size(min = 1, max = 2)
    public String sizeWithMinMax;

    @ConditionalRequired(conditionalFieldName = "conditionalField", matchValues = { "test" })
    public String conditionalRequiredWithMatchValue;

    @ConditionalRequired(conditionalFieldName = "conditionalField")
    public String conditionalRequiredWithoutMatchValue;

    public String conditionalField;
}
