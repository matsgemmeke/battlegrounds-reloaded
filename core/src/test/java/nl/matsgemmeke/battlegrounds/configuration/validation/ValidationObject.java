package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.bukkit.Particle;

public class ValidationObject {

    @Required
    public String required;
    @EnumValue(type = Particle.class)
    public String enumValue;
    @Regex(pattern = "^[a-c]{3}")
    public String regex;
    @ConditionalRequired(conditionalField = "conditionalField", expectedValue = "expected")
    public String conditionalRequiredWithExpectedValue;
    @ConditionalRequired(conditionalField = "conditionalField")
    public String conditionalRequiredWithoutExpectedValue;
    public String conditionalField;
}
