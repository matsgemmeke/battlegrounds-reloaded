package nl.matsgemmeke.battlegrounds.entity.hitbox.resolver;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import org.bukkit.entity.Entity;

@FunctionalInterface
public interface HitboxFactory {

    Hitbox create(Entity entity);
}
