package nl.matsgemmeke.battlegrounds.item.deploy.object;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * A deployment object that is still held by the deployer.
 */
public class HeldDeploymentObject implements DeploymentObject {

    private static final double HAND_HEIGHT_OFFSET = 1.0;

    private final Deployer deployer;
    private final ItemStack itemStack;

    public HeldDeploymentObject(Deployer deployer, ItemStack itemStack) {
        this.deployer = deployer;
        this.itemStack = itemStack;
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public boolean matchesEntity(Entity entity) {
        return false;
    }

    @Override
    public void remove() {
        deployer.removeItem(itemStack);
    }
}
