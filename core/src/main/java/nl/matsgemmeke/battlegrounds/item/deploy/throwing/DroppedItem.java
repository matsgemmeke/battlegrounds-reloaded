package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A deployed item in the form as a dropped {@link Item} entity.
 */
public class DroppedItem implements DeploymentObject, ItemEffectSource, Projectile {

    // An item entity is no living entity, but it has 4 health before getting destroyed
    private static final double ENTITY_HEALTH = 4.0;

    @Nullable
    private Damage lastDamage;
    private double entityHealth;
    private double health;
    @NotNull
    private final Item item;
    private long cooldown;
    @Nullable
    private Map<DamageType, Double> resistances;

    public DroppedItem(@NotNull Item item) {
        this.item = item;
        this.entityHealth = ENTITY_HEALTH;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Nullable
    public Damage getLastDamage() {
        return lastDamage;
    }

    @NotNull
    public Location getLocation() {
        return item.getLocation();
    }

    @Nullable
    public Map<DamageType, Double> getResistances() {
        return resistances;
    }

    public void setResistances(@Nullable Map<DamageType, Double> resistances) {
        this.resistances = resistances;
    }

    @NotNull
    public Vector getVelocity() {
        return item.getVelocity();
    }

    public void setVelocity(@NotNull Vector velocity) {
        item.setVelocity(velocity);
    }

    @NotNull
    public World getWorld() {
        return item.getWorld();
    }

    public boolean hasGravity() {
        return item.hasGravity();
    }

    public void setGravity(boolean gravity) {
        item.setGravity(gravity);
    }

    public double damage(@NotNull Damage damage) {
        if (item.isDead() || !item.isValid()) {
            return 0.0;
        }

        lastDamage = damage;

        double damageAmount = damage.amount();
        DamageType damageType = damage.type();

        if (resistances != null && resistances.containsKey(damageType)) {
            damageAmount *= resistances.get(damage.type());
        }

        if (damageType == DamageType.ENVIRONMENTAL_DAMAGE) {
            entityHealth -= damageAmount;

            if (entityHealth <= 0) {
                health = 0;
            }

            return damageAmount;
        }

        health = Math.max(health - damageAmount, 0);

        return damageAmount;
    }

    public void destroy() {
        this.remove();
    }

    public boolean exists() {
        return !item.isDead();
    }

    public boolean isDeployed() {
        return true;
    }

    public boolean isImmuneTo(@NotNull DamageType damageType) {
        return resistances != null && resistances.containsKey(damageType) && resistances.get(damageType) <= 0;
    }

    public boolean matchesEntity(@NotNull Entity entity) {
        return item == entity;
    }

    public void remove() {
        item.remove();
    }
}
