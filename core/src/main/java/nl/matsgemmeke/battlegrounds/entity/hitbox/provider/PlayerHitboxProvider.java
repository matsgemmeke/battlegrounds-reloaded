package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerHitboxProvider implements HitboxProvider {

    private final PositionHitbox standingHitbox;

    public PlayerHitboxProvider(PositionHitbox standingHitbox) {
        this.standingHitbox = standingHitbox;
    }

    @Override
    public PositionHitbox provideHitbox(Entity entity) {
        if (!(entity instanceof Player player)) {
            throw new HitboxProvisionException("Cannot provide a hitbox for an entity %s as it is not a player".formatted(entity.getType()));
        }

        return standingHitbox;
    }
}
