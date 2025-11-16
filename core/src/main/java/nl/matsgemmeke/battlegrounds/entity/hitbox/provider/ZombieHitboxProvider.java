package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;

public class ZombieHitboxProvider implements HitboxProvider {

    private final RelativeHitbox standingHitboxAdult;
    private final RelativeHitbox standingHitboxBaby;

    public ZombieHitboxProvider(RelativeHitbox standingHitboxAdult, RelativeHitbox standingHitboxBaby) {
        this.standingHitboxAdult = standingHitboxAdult;
        this.standingHitboxBaby = standingHitboxBaby;
    }

    @Override
    public Hitbox provideHitbox(Entity entity) {
        if (!(entity instanceof Zombie zombie)) {
            throw new HitboxProvisionException("Cannot provide a hitbox for an entity %s as it is not a zombie".formatted(entity.getType()));
        }

        Location baseLocation = entity.getLocation();

        if (!zombie.isAdult()) {
            return new Hitbox(baseLocation, standingHitboxBaby);
        }

        return new Hitbox(baseLocation, standingHitboxAdult);
    }
}
