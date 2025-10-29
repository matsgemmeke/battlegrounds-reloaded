package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OpenModeEntity implements GameEntity {

    private final Hitbox hitbox;
    private final LivingEntity entity;
    @Nullable
    private Damage lastDamage;

    public OpenModeEntity(LivingEntity entity, Hitbox hitbox) {
        this.entity = entity;
        this.hitbox = hitbox;
    }

    @NotNull
    public LivingEntity getEntity() {
        return entity;
    }

    public double getHealth() {
        return entity.getHealth();
    }

    public void setHealth(double health) {
        entity.setHealth(health);
    }

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }

    @Nullable
    public Damage getLastDamage() {
        return lastDamage;
    }

    @NotNull
    public Location getLocation() {
        return entity.getLocation();
    }

    @NotNull
    public String getName() {
        return entity.getName();
    }

    public UUID getUniqueId() {
        return entity.getUniqueId();
    }

    @NotNull
    public World getWorld() {
        return entity.getWorld();
    }

    public double damage(@NotNull Damage damage) {
        if (entity.isDead() || entity.getHealth() <= 0.0) {
            return 0.0;
        }

        lastDamage = damage;

        // Divide by 5 to convert to hearts value
        double finalHealth = Math.max(entity.getHealth() - damage.amount() / 5, 0.0);

        // Create fake damage animation
        entity.damage(0.001);
        // Set the health to 0 if the damage is greater than the health
        entity.setHealth(finalHealth);

        return damage.amount();
    }

    public boolean isImmuneTo(@NotNull DamageType damageType) {
        return false;
    }
}
