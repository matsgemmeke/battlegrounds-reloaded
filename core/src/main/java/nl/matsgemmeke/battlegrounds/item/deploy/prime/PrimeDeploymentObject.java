package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an item that is primed but held by the deployer and not yet deployed.
 */
public class PrimeDeploymentObject implements ItemEffectSource {

    private static final double HAND_HEIGHT_OFFSET = 1.0;

    @NotNull
    private final Deployer deployer;
    @NotNull
    private final Entity deployerEntity;
    @NotNull
    private final ItemStack itemStack;

    public PrimeDeploymentObject(@NotNull Deployer deployer, @NotNull Entity deployerEntity, @NotNull ItemStack itemStack) {
        this.deployer = deployer;
        this.deployerEntity = deployerEntity;
        this.itemStack = itemStack;
    }

    @NotNull
    public Location getLocation() {
        return deployerEntity.getLocation().add(0, HAND_HEIGHT_OFFSET, 0);
    }

    public Vector getVelocity() {
        return deployerEntity.getVelocity();
    }

    @NotNull
    public World getWorld() {
        return deployerEntity.getWorld();
    }

    public boolean exists() {
        return !deployerEntity.isDead();
    }

    public void remove() {
        deployer.removeItem(itemStack);
    }
}
