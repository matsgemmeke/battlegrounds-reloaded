package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class DefaultGameMob implements GameMob {

    private static final double DAMAGE_AMOUNT_FOR_ANIMATION = 0.001;

    @NotNull
    private Mob mob;

    public DefaultGameMob(@NotNull Mob mob) {
        this.mob = mob;
    }

    @NotNull
    public Entity getEntity() {
        return mob;
    }

    @NotNull
    public Location getLocation() {
        return mob.getLocation();
    }

    @NotNull
    public World getWorld() {
        return mob.getWorld();
    }

    public double damage(double damageAmount) {
        if (mob.isDead() || mob.getHealth() <= 0.0) {
            return 0.0;
        }

        // Divide by 5 to convert to hearts value
        double finalHealth = Math.max(mob.getHealth() - damageAmount / 5, 0.0);

        // Create fake damage animation
        mob.damage(DAMAGE_AMOUNT_FOR_ANIMATION);
        // Set the health to 0 if the damage is greater than the health
        mob.setHealth(finalHealth);

        return finalHealth;
    }
}
