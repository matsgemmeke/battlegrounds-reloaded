package nl.matsgemmeke.battlegrounds.entity.hitbox.provider;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxUtil;
import nl.matsgemmeke.battlegrounds.entity.hitbox.RelativeHitbox;
import org.bukkit.Location;
import org.bukkit.entity.Villager;

public class VillagerHitboxProvider implements HitboxProvider<Villager> {

    private final RelativeHitbox adultStandingHitbox;
    private final RelativeHitbox adultSleepingHitbox;
    private final RelativeHitbox babyStandingHitbox;
    private final RelativeHitbox babySleepingHitbox;

    public VillagerHitboxProvider(RelativeHitbox adultStandingHitbox, RelativeHitbox adultSleepingHitbox, RelativeHitbox babyStandingHitbox, RelativeHitbox babySleepingHitbox) {
        this.adultStandingHitbox = adultStandingHitbox;
        this.adultSleepingHitbox = adultSleepingHitbox;
        this.babyStandingHitbox = babyStandingHitbox;
        this.babySleepingHitbox = babySleepingHitbox;
    }

    @Override
    public Hitbox provideHitbox(Villager villager) {
        Location baseLocation = villager.getLocation();

        if (villager.isAdult()) {
            return this.createAdultHitbox(baseLocation, villager);
        } else {
            return this.createBabyHitbox(baseLocation, villager);
        }
    }

    private Hitbox createAdultHitbox(Location baseLocation, Villager villager) {
        if (villager.isSleeping()) {
            return this.createSleepingHitbox(baseLocation, adultSleepingHitbox, adultStandingHitbox);
        } else {
            return new Hitbox(baseLocation, adultStandingHitbox);
        }
    }

    private Hitbox createBabyHitbox(Location baseLocation, Villager villager) {
        if (villager.isSleeping()) {
            return this.createSleepingHitbox(baseLocation, babySleepingHitbox, babyStandingHitbox);
        } else {
            return new Hitbox(baseLocation, babyStandingHitbox);
        }
    }

    private Hitbox createSleepingHitbox(Location baseLocation, RelativeHitbox sleepingHitbox, RelativeHitbox fallbackHitbox) {
        Location bedBaseLocation = HitboxUtil.getBedBaseLocation(baseLocation).orElse(null);

        if (bedBaseLocation == null) {
            // Fall back to the standing hitbox if the base location is not a bed
            return new Hitbox(baseLocation, fallbackHitbox);
        }

        return new Hitbox(bedBaseLocation, sleepingHitbox);
    }
}
