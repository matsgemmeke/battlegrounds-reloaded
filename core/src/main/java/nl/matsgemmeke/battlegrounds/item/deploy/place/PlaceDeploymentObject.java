package nl.matsgemmeke.battlegrounds.item.deploy.place;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.object.DeploymentObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A deployed object in the form as a placed {@link Block}.
 */
public class PlaceDeploymentObject implements DeploymentObject, DamageTarget {

    private static final double BLOCK_CENTER_OFFSET = 0.5;
    private static final double BOUNDING_BOX_SIZE = 0.2;

    private final Block block;
    private final DestructionListener destructionListener;
    private final HitboxProvider<StaticBoundingBox> hitboxProvider;
    private final Material material;
    private final UUID uniqueId;
    @Nullable
    private Damage lastDamage;
    private double health;
    @Nullable
    private Map<DamageType, Double> resistances;

    public PlaceDeploymentObject(Block block, Material material, HitboxProvider<StaticBoundingBox> hitboxProvider, DestructionListener destructionListener) {
        this.block = block;
        this.material = material;
        this.hitboxProvider = hitboxProvider;
        this.destructionListener = destructionListener;
        this.uniqueId = UUID.randomUUID();
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Override
    public Optional<Damage> getLastDamage() {
        return Optional.ofNullable(lastDamage);
    }

    @NotNull
    public Location getLocation() {
        return block.getLocation().add(BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET);
    }

    @Nullable
    public Map<DamageType, Double> getResistances() {
        return resistances;
    }

    public void setResistances(@Nullable Map<DamageType, Double> resistances) {
        this.resistances = resistances;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Vector getVelocity() {
        // Blocks do not move, so it always has a vector of zero
        return new Vector().zero();
    }

    @NotNull
    public World getWorld() {
        return block.getWorld();
    }

    @Override
    public double damage(Damage damage) {
        lastDamage = damage;

        double damageAmount = damage.amount();

        if (resistances != null && resistances.containsKey(damage.type())) {
            damageAmount *= resistances.get(damage.type());
        }

        health = Math.max(health - damageAmount, 0);

        if (health <= 0.0) {
            destructionListener.onDestroyed(damage);
        }

        return damageAmount;
    }

    public boolean exists() {
        return block.getType() == material;
    }

    @Override
    public Hitbox getHitbox() {
        Location baseLocation = block.getLocation();
        StaticBoundingBox boundingBox = new StaticBoundingBox(baseLocation, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);

        return hitboxProvider.provideHitbox(boundingBox);
    }

    @Override
    public boolean isPhysical() {
        return true;
    }

    public boolean matchesEntity(@NotNull Entity entity) {
        // A placed block is never an entity, so always return false
        return false;
    }

    public void remove() {
        block.setType(Material.AIR);
    }
}
