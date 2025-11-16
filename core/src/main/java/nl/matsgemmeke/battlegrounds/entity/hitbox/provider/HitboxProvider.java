package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import org.bukkit.entity.Entity;

public interface HitboxProvider {

    /**
     * Returns the hitbox corresponding with the given entity's current state and pose.
     *
     * @param entity the entity
     * @return       the corresponding hitbox
     */
    Hitbox provideHitbox(Entity entity);
}
