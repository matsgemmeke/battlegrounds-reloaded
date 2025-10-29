package nl.matsgemmeke.battlegrounds.entity.hitbox;

import org.bukkit.entity.Entity;

@FunctionalInterface
public interface HitboxFactory<T extends Entity> {

    Hitbox create(T entity);
}
