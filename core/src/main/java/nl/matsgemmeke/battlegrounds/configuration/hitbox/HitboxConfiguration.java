package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BasePluginConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.configuration.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationException;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class HitboxConfiguration extends BasePluginConfiguration {

    private static final boolean READ_ONLY = true;
    private static final String HITBOX_SECTION_ROOT = "hitbox";

    private static final String HITBOX_COMPONENT_TYPE_FALLBACK = "TORSO";
    private static final List<Double> HITBOX_COMPONENT_SIZE_FALLBACK = Collections.emptyList();
    private static final List<Double> HITBOX_COMPONENT_OFFSET_FALLBACK = Collections.emptyList();

    public HitboxConfiguration(File file, InputStream resource) {
        super(file, resource, READ_ONLY);
    }

    public Optional<HitboxDefinition> getHitboxDefinition(String entityType, String position) {
        String sectionRoute = this.createSectionRoute(entityType, position);
        Section section = this.getOptionalSection(sectionRoute).orElse(null);

        if (section == null) {
            return Optional.empty();
        }

        List<HitboxComponentDefinition> componentDefinitions = new ArrayList<>();
        Map<String, Object> hitboxDefinitions = section.getStringRouteMappedValues(false);

        for (Object hitboxDefinition : hitboxDefinitions.values()) {
            Section hitboxDefinitionSection = (Section) hitboxDefinition;

            String type = hitboxDefinitionSection.getOptionalString("type").orElse(HITBOX_COMPONENT_TYPE_FALLBACK);
            Double[] size = hitboxDefinitionSection.getOptionalDoubleList("size").orElse(HITBOX_COMPONENT_SIZE_FALLBACK).toArray(Double[]::new);
            Double[] offset = hitboxDefinitionSection.getOptionalDoubleList("offset").orElse(HITBOX_COMPONENT_OFFSET_FALLBACK).toArray(Double[]::new);

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

    private String createSectionRoute(String entityType, String position) {
        return HITBOX_SECTION_ROOT + "-" + entityType + "." + position;
    }
}
