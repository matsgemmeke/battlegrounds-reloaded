package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

/**
 * Hitbox provider for ageable entities with only a standing pose.
 */
public class AgeableHitboxProvider implements HitboxProvider {

    private final RelativeHitbox standingHitboxAdult;
    private final RelativeHitbox standingHitboxBaby;

    public AgeableHitboxProvider(RelativeHitbox standingHitboxAdult, RelativeHitbox standingHitboxBaby) {
        this.standingHitboxAdult = standingHitboxAdult;
        this.standingHitboxBaby = standingHitboxBaby;
    }

    @Override
    public Hitbox provideHitbox(Entity entity) {
        if (!(entity instanceof Ageable ageableEntity)) {
            throw new HitboxProvisionException("Cannot provide a hitbox for an entity %s as it is not an ageable entity".formatted(entity.getType()));
        }

        Location baseLocation = ageableEntity.getLocation();

        if (!ageableEntity.isAdult()) {
            return new Hitbox(baseLocation, standingHitboxBaby);
        }

        return new Hitbox(baseLocation, standingHitboxAdult);
    }
}
