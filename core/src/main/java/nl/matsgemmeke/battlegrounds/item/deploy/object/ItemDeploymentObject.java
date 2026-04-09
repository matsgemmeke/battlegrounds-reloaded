package nl.matsgemmeke.battlegrounds.item.deploy.object;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A deployment object represented by an {@link Item} entity.
 */
public class ItemDeploymentObject implements DeploymentObject, DamageTarget, Projectile {

    // An item entity is no living entity, but it has 4 health before getting destroyed
    private static final double ENTITY_HEALTH = 4.0;

    private final DestructionListener destructionListener;
    private final HitboxProvider<StaticBoundingBox> hitboxProvider;
    private final Item item;
    private final Map<DamageType, Double> resistances;
    private final UUID uniqueId;
    @Nullable
    private Damage lastDamage;
    private double entityHealth;
    private double health;

    public ItemDeploymentObject(Item item, HitboxProvider<StaticBoundingBox> hitboxProvider, DestructionListener destructionListener) {
        this.item = item;
        this.hitboxProvider = hitboxProvider;
        this.destructionListener = destructionListener;
        this.entityHealth = ENTITY_HEALTH;
        this.resistances = new HashMap<>();
        this.uniqueId = UUID.randomUUID();
    }

    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public void setHealth(double health) {
        this.health = health;
    }

    @Override
    public Optional<Damage> getLastDamage() {
        return Optional.ofNullable(lastDamage);
    }

    @Override
    public Location getLocation() {
        return item.getLocation();
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public Vector getVelocity() {
        return item.getVelocity();
    }

    @Override
    public void setVelocity(Vector velocity) {
        item.setVelocity(velocity);
    }

    @Override
    public World getWorld() {
        return item.getWorld();
    }

    @Override
    public boolean hasGravity() {
        return item.hasGravity();
    }

    @Override
    public void setGravity(boolean gravity) {
        item.setGravity(gravity);
    }

    @Override
    public boolean isPhysical() {
        return true;
    }

    public void addResistance(DamageType type, double factor) {
        resistances.put(type, factor);
    }

    @Override
    public double damage(Damage damage) {
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
                destructionListener.onDestroyed(damage);
            }

            return damageAmount;
        }

        health = Math.max(health - damageAmount, 0.0);

        if (health <= 0.0) {
            destructionListener.onDestroyed(damage);
        }

        return damageAmount;
    }

    @Override
    public boolean exists() {
        return !item.isDead();
    }

    @Override
    public Hitbox getHitbox() {
        Location baseLocation = item.getLocation();
        BoundingBox boundingBox = item.getBoundingBox();
        StaticBoundingBox staticBoundingBox = new StaticBoundingBox(baseLocation, boundingBox.getWidthX(), boundingBox.getHeight(), boundingBox.getWidthZ());

        return hitboxProvider.provideHitbox(staticBoundingBox);
    }

    @Override
    public boolean matchesEntity(Entity entity) {
        return item == entity;
    }

    @Override
    public void remove() {
        item.remove();
    }
}
