package nl.matsgemmeke.battlegrounds.item.actor;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class HeldItemActor implements Actor, Removable {

    // Currently only works for humanoid entities
    private static final double HAND_HEIGHT_OFFSET = 1.0;

    private final Deployer deployer;
    private final GameEntity gameEntity;
    private final ItemStack itemStack;

    public HeldItemActor(Deployer deployer, GameEntity gameEntity, ItemStack itemStack) {
        this.deployer = deployer;
        this.gameEntity = gameEntity;
        this.itemStack = itemStack;
    }

    @Override
    public boolean exists() {
        return gameEntity.isValid();
    }

    @Override
    public Location getLocation() {
        return gameEntity.getLocation().add(0, HAND_HEIGHT_OFFSET, 0);
    }

    @Override
    public Vector getVelocity() {
        return gameEntity.getVelocity();
    }

    @Override
    public World getWorld() {
        return gameEntity.getWorld();
    }

    @Override
    public void remove() {
        deployer.removeItem(itemStack);
    }
}
