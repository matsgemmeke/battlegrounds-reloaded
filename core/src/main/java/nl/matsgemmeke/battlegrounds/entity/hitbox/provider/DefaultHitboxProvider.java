package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.entity.Entity;

/**
 * Hitbox provider for entities with only a standing pose.
 */
public class DefaultHitboxProvider implements HitboxProvider {

    private final RelativeHitbox standingHitbox;

    public DefaultHitboxProvider(RelativeHitbox standingHitbox) {
        this.standingHitbox = standingHitbox;
    }

    @Override
    public Hitbox provideHitbox(Entity entity) {
        return new Hitbox(entity.getLocation(), standingHitbox);
    }
}
