package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import nl.matsgemmeke.battlegrounds.configuration.BasePluginConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.ValidationException;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class HitboxConfiguration extends BasePluginConfiguration {

    private static final boolean READ_ONLY = true;

    private static final String HITBOX_COMPONENT_TYPE_FALLBACK = "TORSO";

    private final ObjectValidator objectValidator;

    public HitboxConfiguration(ObjectValidator objectValidator, File file, InputStream resource) {
        super(file, resource, READ_ONLY);
        this.objectValidator = objectValidator;
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

            componentDefinitions.add(componentDefinition);
        }

        HitboxDefinition hitboxDefinition = new HitboxDefinition();
        hitboxDefinition.components = componentDefinitions;

        try {
            objectValidator.validate(hitboxDefinition);
        } catch (ValidationException ex) {
            return Optional.empty();
        }

        return Optional.of(hitboxDefinition);
    }
}
