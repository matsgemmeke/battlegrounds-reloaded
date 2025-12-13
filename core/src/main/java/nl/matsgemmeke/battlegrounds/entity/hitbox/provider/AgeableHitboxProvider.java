package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;

/**
 * Hitbox provider for ageable entities with only a standing pose.
 */
public class AgeableHitboxProvider<T extends Ageable> implements HitboxProviderNew<T> {

    private final RelativeHitbox standingHitboxAdult;
    private final RelativeHitbox standingHitboxBaby;

    public AgeableHitboxProvider(RelativeHitbox standingHitboxAdult, RelativeHitbox standingHitboxBaby) {
        this.standingHitboxAdult = standingHitboxAdult;
        this.standingHitboxBaby = standingHitboxBaby;
    }

    @Override
    public Hitbox provideHitbox(Ageable ageableEntity) {
        Location baseLocation = ageableEntity.getLocation();

        if (!ageableEntity.isAdult()) {
            return new Hitbox(baseLocation, standingHitboxBaby);
        }

        return new Hitbox(baseLocation, standingHitboxAdult);
    }
}
