package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import nl.matsgemmeke.battlegrounds.configuration.BasePluginConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.configuration.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationException;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

public class HitboxConfiguration extends BasePluginConfiguration {

    private static final boolean READ_ONLY = true;

    private static final String HITBOX_COMPONENT_TYPE_FALLBACK = "TORSO";

    public HitboxConfiguration(File file, InputStream resource) {
        super(file, resource, READ_ONLY);
    }

    public Optional<HitboxDefinition> getHitboxDefinition(String entityType, String position) {
        ConfigurationSection section = this.getOptionalSection(entityType + "." + position).orElse(null);

        if (section == null) {
            return Optional.empty();
        }

        List<HitboxComponentDefinition> componentDefinitions = new ArrayList<>();
        Map<String, Object> hitboxDefinitions = section.getValues(false);

        for (Object hitboxDefinition : hitboxDefinitions.values()) {
            ConfigurationSection hitboxDefinitionSection = (ConfigurationSection) hitboxDefinition;

            String type = Optional.ofNullable(hitboxDefinitionSection.getString("type")).orElse(HITBOX_COMPONENT_TYPE_FALLBACK);
            Double[] size = hitboxDefinitionSection.getDoubleList("size").toArray(Double[]::new);
            Double[] offset = hitboxDefinitionSection.getDoubleList("offset").toArray(Double[]::new);

            HitboxComponentDefinition componentDefinition = new HitboxComponentDefinition();
            componentDefinition.type = type;
            componentDefinition.size = size;
            componentDefinition.offset = offset;

            try {
                ObjectValidator.validate(componentDefinition);
            } catch (ValidationException e) {
                throw new InvalidHitboxDefinitionException("Validation failed for the hitbox definition for %s for the position %s: %s".formatted(entityType, position, e.getMessage()));
            }

            componentDefinitions.add(componentDefinition);
        }

        HitboxDefinition hitboxDefinition = new HitboxDefinition();
        hitboxDefinition.components = componentDefinitions;

        return Optional.of(hitboxDefinition);
    }
}
