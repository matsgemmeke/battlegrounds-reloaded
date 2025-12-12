package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;

public interface HitboxProviderNew<T> {

    /**
     * Returns the hitbox corresponding with the given object.
     *
     * @param object the object
     * @return       the corresponding hitbox
     */
    Hitbox provideHitbox(T object);
}
