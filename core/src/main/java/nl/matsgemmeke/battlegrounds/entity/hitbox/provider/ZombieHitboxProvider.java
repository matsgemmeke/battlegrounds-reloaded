package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.PositionHitbox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;

public class ZombieHitboxProvider implements HitboxProvider {

    private final PositionHitbox standingHitboxAdult;
    private final PositionHitbox standingHitboxBaby;

    public ZombieHitboxProvider(PositionHitbox standingHitboxAdult, PositionHitbox standingHitboxBaby) {
        this.standingHitboxAdult = standingHitboxAdult;
        this.standingHitboxBaby = standingHitboxBaby;
    }

    @Override
    public PositionHitbox provideHitbox(Entity entity) {
        if (!(entity instanceof Zombie zombie)) {
            throw new HitboxProvisionException("Cannot provide a hitbox for an entity %s as it is not a zombie".formatted(entity.getType()));
        }

        if (!zombie.isAdult()) {
            return standingHitboxBaby;
        }

        return standingHitboxAdult;
    }
}
