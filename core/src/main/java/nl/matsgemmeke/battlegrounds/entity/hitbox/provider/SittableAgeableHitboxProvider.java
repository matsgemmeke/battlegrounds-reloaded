package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Sittable;

/**
 * Hitbox provider for ageable entities that are able to stand and sit.
 */
public class SittableAgeableHitboxProvider<T extends Ageable & Sittable> implements HitboxProvider<T> {

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
    public Hitbox provideHitbox(T entity) {
        Location baseLocation = entity.getLocation();

        if (entity.isAdult()) {
            return this.createAdultHitbox(baseLocation, entity);
        } else {
            return this.createBabyHitbox(baseLocation, entity);
        }
    }

    private Hitbox createAdultHitbox(Location baseLocation, T entity) {
        if (entity.isSitting()) {
            return new Hitbox(baseLocation, adultSittingHitbox);
        } else {
            return new Hitbox(baseLocation, adultStandingHitbox);
        }
    }

    private Hitbox createBabyHitbox(Location baseLocation, T entity) {
        if (entity.isSitting()) {
            return new Hitbox(baseLocation, babySittingHitbox);
        } else {
            return new Hitbox(baseLocation, babyStandingHitbox);
        }
    }
}
