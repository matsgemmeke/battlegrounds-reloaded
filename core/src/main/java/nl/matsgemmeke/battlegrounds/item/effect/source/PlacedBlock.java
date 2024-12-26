package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * A deployed object in the form as a placed {@link Block}.
 */
public class PlacedBlock implements DeploymentObject, EffectSource {

    private static final double BLOCK_CENTER_OFFSET = 0.5;

    @NotNull
    private Block block;
    private double health;
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

    @NotNull
    public World getWorld() {
        return block.getWorld();
    }

    public boolean isDeployed() {
        return true;
    }

    public double damage(double damageAmount) {
        double healthAfterDamage = health - damageAmount;

        health = Math.max(healthAfterDamage, 0);

        return health;
    }

    public void destroy() {
        this.remove();
    }

    public void remove() {
        block.setType(Material.AIR);
    }
}
