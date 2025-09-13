package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A deployment system that produces a {@link DeploymentObject} using an {@link Item}.
 */
public class ThrowDeployment implements Deployment {

    // Take a high number to make sure the item cannot be picked up before the deployment is complete
    private static final int DEFAULT_PICKUP_DELAY = 100000;

    @NotNull
    private final AudioEmitter audioEmitter;
    @Nullable
    private ThrowDeploymentProperties properties;

    @Inject
    public ThrowDeployment(@NotNull AudioEmitter audioEmitter) {
        this.audioEmitter = audioEmitter;
    }

    public void configureProperties(ThrowDeploymentProperties properties) {
        this.properties = properties;
    }

    @NotNull
    public DeploymentResult perform(@NotNull Deployer deployer, @NotNull Entity deployerEntity) {
        if (properties == null) {
            throw new IllegalStateException("Cannot perform deployment without properties configured");
        }

        Location deployLocation = deployer.getDeployLocation();
        Vector velocity = deployer.getDeployLocation().getDirection().multiply(properties.velocity());
        ItemStack itemStack = properties.itemTemplate().createItemStack();
        World world = deployerEntity.getWorld();

        Item item = world.dropItem(deployLocation, itemStack);
        item.setPickupDelay(DEFAULT_PICKUP_DELAY);
        item.setVelocity(velocity);

        ThrowDeploymentObject object = new ThrowDeploymentObject(item);
        object.setCooldown(properties.cooldown());
        object.setHealth(properties.health());
        object.setResistances(properties.resistances());

        properties.projectileEffects().forEach(effect -> effect.onLaunch(deployerEntity, object));

        audioEmitter.playSounds(properties.throwSounds(), deployLocation);

        deployer.setHeldItem(null);

        return DeploymentResult.success(object);
    }
}
