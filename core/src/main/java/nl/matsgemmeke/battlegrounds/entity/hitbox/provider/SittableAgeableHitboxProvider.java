package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sittable;

/**
 * Hitbox provider for ageable entities that are able to stand and sit.
 */
public class SittableAgeableHitboxProvider implements HitboxProvider {

    private final RelativeHitbox adultStandingHitbox;
    private final RelativeHitbox adultSittingHitbox;
    private final RelativeHitbox babyStandingHitbox;
    private final RelativeHitbox babySittingHitbox;

    public SittableAgeableHitboxProvider(RelativeHitbox adultStandingHitbox, RelativeHitbox adultSittingHitbox, RelativeHitbox babyStandingHitbox, RelativeHitbox babySittingHitbox) {
        this.adultStandingHitbox = adultStandingHitbox;
        this.adultSittingHitbox = adultSittingHitbox;
        this.babyStandingHitbox = babyStandingHitbox;
        this.babySittingHitbox = babySittingHitbox;
    }

    @Override
    public Hitbox provideHitbox(Entity entity) {
        if (!(entity instanceof Sittable sittableEntity) || !(entity instanceof Ageable ageableEntity)) {
            throw new HitboxProvisionException("Entity %s is not compatible: expected a sittable and ageable entity".formatted(entity.getType()));
        }

        Location baseLocation = entity.getLocation();

        if (ageableEntity.isAdult()) {
            return this.createAdultHitbox(baseLocation, sittableEntity);
        } else {
            return this.createBabyHitbox(baseLocation, sittableEntity);
        }
    }

    private Hitbox createAdultHitbox(Location baseLocation, Sittable sittableEntity) {
        if (sittableEntity.isSitting()) {
            return new Hitbox(baseLocation, adultSittingHitbox);
        } else {
            return new Hitbox(baseLocation, adultStandingHitbox);
        }
    }

    private Hitbox createBabyHitbox(Location baseLocation, Sittable sittableEntity) {
        if (sittableEntity.isSitting()) {
            return new Hitbox(baseLocation, babySittingHitbox);
        } else {
            return new Hitbox(baseLocation, babyStandingHitbox);
        }
    }
}
