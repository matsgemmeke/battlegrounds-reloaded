package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerHitboxProvider implements HitboxProvider {

    private final RelativeHitbox standingHitbox;

    public PlayerHitboxProvider(RelativeHitbox standingHitbox) {
        this.standingHitbox = standingHitbox;
    }

    @Override
    public PositionHitbox provideHitbox(Entity entity) {
        if (!(entity instanceof Player player)) {
            throw new HitboxProvisionException("Cannot provide a hitbox for an entity %s as it is not a player".formatted(entity.getType()));
        }

        Location baseLocation = player.getLocation();

        return new PositionHitbox(baseLocation, standingHitbox);
    }
}
