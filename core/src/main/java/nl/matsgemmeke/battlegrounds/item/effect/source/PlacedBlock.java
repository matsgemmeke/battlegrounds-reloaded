package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A deployed object in the form as a placed {@link Block}.
 */
public class PlacedBlock implements DeploymentObject, EffectSource {

    private static final double BLOCK_CENTER_OFFSET = 0.5;

    @NotNull
    private Block block;
    private double health;
    @Nullable
    private Map<DamageType, Double> resistances;
    @NotNull
    private Material material;

    public PlacedBlock(@NotNull Block block, @NotNull Material material) {
        this.block = block;
        this.material = material;
    }

    public boolean exists() {
        return block.getType() == material;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
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

    @NotNull
    public World getWorld() {
        return block.getWorld();
    }

    public boolean isDeployed() {
        return true;
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
