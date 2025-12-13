package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.util.HitboxUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

public class PlayerHitboxProvider implements HitboxProviderNew<Player> {

    private final RelativeHitbox standingHitbox;
    private final RelativeHitbox sneakingHitbox;
    private final RelativeHitbox sleepingHitbox;

    public PlayerHitboxProvider(RelativeHitbox standingHitbox, RelativeHitbox sneakingHitbox, RelativeHitbox sleepingHitbox) {
        this.standingHitbox = standingHitbox;
        this.sneakingHitbox = sneakingHitbox;
        this.sleepingHitbox = sleepingHitbox;
    }

    @Override
    public Hitbox provideHitbox(Player player) {
        Location baseLocation = player.getLocation();

        if (player.getPose() == Pose.SNEAKING) {
            return new Hitbox(baseLocation, sneakingHitbox);
        }

        if (player.getPose() == Pose.SLEEPING) {
            return this.createSleepingPoseHitbox(baseLocation);
        }

        return new Hitbox(baseLocation, standingHitbox);
    }

    private Hitbox createSleepingPoseHitbox(Location baseLocation) {
        Location bedBaseLocation = HitboxUtil.getBedBaseLocation(baseLocation).orElse(null);

        if (bedBaseLocation == null) {
            // Fall back to the standing hitbox if the base location is not a bed
            return new Hitbox(baseLocation, standingHitbox);
        }

        return new Hitbox(bedBaseLocation, sleepingHitbox);
    }
}
