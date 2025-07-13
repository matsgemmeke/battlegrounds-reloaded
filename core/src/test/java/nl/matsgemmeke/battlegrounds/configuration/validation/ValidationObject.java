package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.bukkit.Particle;

public class ValidationObject {

    @Required
    public String required;
    @EnumValue(type = Particle.class)
    public String enumValue;
    @Regex(pattern = "^[a-c]{3}")
    public String regex;
    @ConditionalRequired(conditionalFieldName = "conditionalField", matchValues = { "test" })
    public String conditionalRequiredWithMatchValue;
    @ConditionalRequired(conditionalFieldName = "conditionalField")
    public String conditionalRequiredWithoutMatchValue;
    public String conditionalField;
}
