package nl.matsgemmeke.battlegrounds.item.deploy.place;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.EffectSource;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * A deployed object in the form as a placed {@link Block}.
 */
public class PlaceDeploymentObject implements DeploymentObject, EffectSource {

    private static final double BLOCK_CENTER_OFFSET = 0.5;

    private final UUID uniqueId;
    @NotNull
    private Block block;
    @Nullable
    private Damage lastDamage;
    private double health;
    private long cooldown;
    @Nullable
    private Map<DamageType, Double> resistances;
    @NotNull
    private Material material;

    public PlaceDeploymentObject(@NotNull Block block, @NotNull Material material) {
        this.block = block;
        this.material = material;
        this.uniqueId = UUID.randomUUID();
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

    public boolean isDeployed() {
        return true;
    }

    public double damage(@NotNull Damage damage) {
        lastDamage = damage;

        double damageAmount = damage.amount();

        if (resistances != null && resistances.containsKey(damage.type())) {
            damageAmount *= resistances.get(damage.type());
        }

        health = Math.max(health - damageAmount, 0);

        return damageAmount;
    }

    public boolean exists() {
        return block.getType() == material;
    }

    public boolean isImmuneTo(@NotNull DamageType damageType) {
        return resistances != null && resistances.containsKey(damageType) && resistances.get(damageType) == 0;
    }

    public boolean matchesEntity(@NotNull Entity entity) {
        // A placed block is never an entity, so always return false
        return false;
    }

    public void remove() {
        block.setType(Material.AIR);
    }
}
