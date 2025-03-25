package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A deployment system that produces a {@link DeploymentObject} using an {@link Item}.
 */
public class ThrowDeployment implements Deployment {

    // Take a high number to make sure the item cannot be picked up before the deployment is complete
    private static final int DEFAULT_PICKUP_DELAY = 100000;

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final ThrowDeploymentProperties deploymentProperties;

    public ThrowDeployment(@NotNull ThrowDeploymentProperties deploymentProperties, @NotNull AudioEmitter audioEmitter) {
        this.deploymentProperties = deploymentProperties;
        this.audioEmitter = audioEmitter;
    }

    @NotNull
    public DeploymentResult perform(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        Location deployLocation = deployer.getDeployLocation();
        Vector velocity = deployer.getDeployLocation().getDirection().multiply(deploymentProperties.velocity());
        ItemStack itemStack = deploymentProperties.itemTemplate().createItemStack();
        World world = deployerEntity.getWorld();

        Item item = world.dropItem(deployLocation, itemStack);
        item.setPickupDelay(DEFAULT_PICKUP_DELAY);
        item.setVelocity(velocity);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.setCooldown(deploymentProperties.cooldown());
        object.setHealth(deploymentProperties.health());
        object.setResistances(deploymentProperties.resistances());

        deploymentProperties.projectileEffects().forEach(effect -> effect.onLaunch(object));

        audioEmitter.playSounds(deploymentProperties.throwSounds(), deployLocation);

        deployer.setHeldItem(null);

        return DeploymentResult.success(object);
    }
}
