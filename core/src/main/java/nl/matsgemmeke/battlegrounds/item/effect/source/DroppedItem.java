package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A deployed item in the form as a dropped {@link Item} entity.
 */
public class DroppedItem implements DeploymentObject, EffectSource, Projectile {

    private double health;
    @NotNull
    private Item itemEntity;

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

    public double damage(double damageAmount) {
        double healthAfterDamage = health - damageAmount;

        health = Math.max(healthAfterDamage, 0);

        return health;
    }

    public void destroy() {
        this.remove();
    }

    public boolean isDeployed() {
        return true;
    }

    public void remove() {
        itemEntity.remove();
    }
}
