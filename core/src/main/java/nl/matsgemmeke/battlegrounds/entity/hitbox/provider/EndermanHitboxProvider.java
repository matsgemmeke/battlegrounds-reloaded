package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Enderman;

public class EndermanHitboxProvider implements HitboxProvider<Enderman> {

    private final RelativeHitbox carryingHitbox;
    private final RelativeHitbox standingHitbox;

    public EndermanHitboxProvider(RelativeHitbox standingHitbox, RelativeHitbox carryingHitbox) {
        this.standingHitbox = standingHitbox;
        this.carryingHitbox = carryingHitbox;
    }

    @Override
    public Hitbox provideHitbox(Enderman enderman) {
        Location baseLocation = enderman.getLocation();

        if (enderman.getCarriedBlock() != null) {
            return new Hitbox(baseLocation, carryingHitbox);
        }

        return new Hitbox(baseLocation, standingHitbox);
    }
}
