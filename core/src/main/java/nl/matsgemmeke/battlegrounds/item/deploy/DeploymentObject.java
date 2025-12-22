package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.Target;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
import org.bukkit.entity.Entity;

/**
 * Represents an object that is produced as a result of a deployment action.
 */
public interface DeploymentObject extends DamageTarget, Target, ItemEffectSource, Removable {

    /**
     * Returns whether the deployment object has a physical embodiment.
     *
     * @return whether the deployment object is physical
     */
    boolean isPhysical();

    /**
     * Gets whether the object matches with a given entity. Returns {@code true} if the object encapsulates the given
     * entity, otherwise {@code false}.
     *
     * @param entity the entity
     * @return whether the object matches with the entity
     */
    boolean matchesEntity(Entity entity);
}
