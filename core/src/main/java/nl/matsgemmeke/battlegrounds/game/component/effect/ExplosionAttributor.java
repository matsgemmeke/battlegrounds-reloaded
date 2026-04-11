package nl.matsgemmeke.battlegrounds.game.component.effect;

import java.util.UUID;

/**
 * Represents an entity that is used to attribute an explosion with a source.
 *
 * @param entityId the entity unique id
 */
public record ExplosionAttributor(UUID entityId) {
}
