package nl.matsgemmeke.battlegrounds.item.deploy.object;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A deployed object represented by a placed {@link Block}.
 */
public class BlockDeploymentObject implements DeploymentObject, DamageTarget {

    private static final double BLOCK_CENTER_OFFSET = 0.5;
    private static final double BOUNDING_BOX_SIZE = 0.2;

    private final Block block;
    private final DestructionListener destructionListener;
    private final HitboxProvider<StaticBoundingBox> hitboxProvider;
    private final Map<DamageType, Double> resistances;
    private final Material material;
    private final UUID uniqueId;
    @Nullable
    private Damage lastDamage;
    private double health;

    public BlockDeploymentObject(Block block, Material material, HitboxProvider<StaticBoundingBox> hitboxProvider, DestructionListener destructionListener) {
        this.block = block;
        this.material = material;
        this.hitboxProvider = hitboxProvider;
        this.destructionListener = destructionListener;
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
        return block.getLocation().add(BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET, BLOCK_CENTER_OFFSET);
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    public void addResistance(DamageType damageType, double factor) {
        resistances.put(damageType, factor);
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

    @Override
    public boolean matchesEntity(@NotNull Entity entity) {
        // A placed block is never an entity, so always return false
        return false;
    }

    public void remove() {
        block.setType(Material.AIR);
    }
}
