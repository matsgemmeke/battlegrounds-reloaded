package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.item.effect.source.RemovableItemEffectSource;
import org.bukkit.entity.Entity;

/**
 * Represents an object that is produced as a result of a deployment action.
 */
public interface DeploymentObject extends GameEntity, RemovableItemEffectSource {

    /**
     * Gets whether the object matches with a given entity. Returns {@code true} if the object encapsulates the given
     * entity, otherwise {@code false}.
     *
     * @param entity the entity
     * @return whether the object matches with the entity
     */
    boolean matchesEntity(Entity entity);
}
