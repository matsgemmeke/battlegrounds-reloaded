package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
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
public class DroppedItem implements DeploymentObject, EffectSource, Projectile {

    private double health;
    @NotNull
    private Item itemEntity;
    @Nullable
    private Map<DamageType, Double> resistances;

    public DroppedItem(@NotNull Item itemEntity) {
        this.itemEntity = itemEntity;
    }

    public boolean exists() {
        return !itemEntity.isDead();
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @NotNull
    public Location getLocation() {
        return itemEntity.getLocation();
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
        return itemEntity.getVelocity();
    }

    public void setVelocity(@NotNull Vector velocity) {
        itemEntity.setVelocity(velocity);
    }

    @NotNull
    public World getWorld() {
        return itemEntity.getWorld();
    }

    public boolean hasGravity() {
        return itemEntity.hasGravity();
    }

    public void setGravity(boolean gravity) {
        itemEntity.setGravity(gravity);
    }

    public double damage(@NotNull Damage damage) {
        double damageAmount = damage.amount();

        if (resistances != null && resistances.containsKey(damage.type())) {
            damageAmount *= resistances.get(damage.type());
        }

        health = Math.max(health - damageAmount, 0);

        return damageAmount;
    }

    public void destroy() {
        this.remove();
    }

    public boolean isDeployed() {
        return true;
    }

    public boolean isImmuneTo(@NotNull DamageType damageType) {
        return resistances != null && resistances.containsKey(damageType) && resistances.get(damageType) <= 0;
    }

    public boolean matchesEntity(@NotNull Entity entity) {
        return itemEntity == entity;
    }

    public void remove() {
        itemEntity.remove();
    }
}
