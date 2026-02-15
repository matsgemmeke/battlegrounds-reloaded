package nl.matsgemmeke.battlegrounds.configuration.spec;

import java.util.Map;

public record PolymorphicDefinition(String discriminator, Map<String, Class<?>> mappings) {
}
