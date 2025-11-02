package nl.matsgemmeke.battlegrounds.entity.hitbox.mapper;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;

import java.util.HashSet;
import java.util.Set;

/**
 * Mapper that converts {@link HitboxDefinition} objects loaded from configuration files into {@link PositionHitbox}
 * instances.
 */
public class HitboxMapper {

    public PositionHitbox map(HitboxDefinition hitboxDefinition) {
        Set<HitboxComponent> components = new HashSet<>();

        for (HitboxComponentDefinition componentDefinition : hitboxDefinition.components) {
            HitboxComponentType type = HitboxComponentType.valueOf(componentDefinition.type);
            double height = componentDefinition.size[0];
            double width = componentDefinition.size[1];
            double depth = componentDefinition.size[2];
            double offsetX = componentDefinition.offset[0];
            double offsetY = componentDefinition.offset[1];
            double offsetZ = componentDefinition.offset[2];

            components.add(new HitboxComponent(type, height, width, depth, offsetX, offsetY, offsetZ));
        }

        return new PositionHitbox(components);
    }
}
