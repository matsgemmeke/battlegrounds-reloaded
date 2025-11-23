package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;

public class EndermanHitboxProvider implements HitboxProvider {

    private final RelativeHitbox carryingHitbox;
    private final RelativeHitbox standingHitbox;

    public EndermanHitboxProvider(RelativeHitbox standingHitbox, RelativeHitbox carryingHitbox) {
        this.standingHitbox = standingHitbox;
        this.carryingHitbox = carryingHitbox;
    }

    @Override
    public Hitbox provideHitbox(Entity entity) {
        if (!(entity instanceof Enderman enderman)) {
            throw new HitboxProvisionException("Cannot provide a hitbox for an entity %s as it is not an enderman".formatted(entity.getType()));
        }

        Location baseLocation = entity.getLocation();

        if (enderman.getCarriedBlock() != null) {
            return new Hitbox(baseLocation, carryingHitbox);
        }

        return new Hitbox(baseLocation, standingHitbox);
    }
}
