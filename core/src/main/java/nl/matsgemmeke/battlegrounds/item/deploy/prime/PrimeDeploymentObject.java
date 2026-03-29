package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an item that is primed but held by the deployer and not yet deployed.
 */
public class PrimeDeploymentObject implements DeploymentObject {

    private static final double HAND_HEIGHT_OFFSET = 1.0;

    private final Deployer deployer;
    private final Entity deployerEntity;
    private final ItemStack itemStack;

    public PrimeDeploymentObject(Deployer deployer, Entity deployerEntity, ItemStack itemStack) {
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

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public boolean matchesEntity(Entity entity) {
        return false;
    }

    public void remove() {
        deployer.removeItem(itemStack);
    }
}
