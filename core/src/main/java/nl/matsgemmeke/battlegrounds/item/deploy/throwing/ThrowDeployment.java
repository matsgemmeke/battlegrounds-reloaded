package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A deployment system that produces a {@link DeploymentObject} using an {@link Item}.
 */
public class ThrowDeployment implements Deployment {

    // Take a high number to make sure the item cannot be picked up before the deployment is complete
    private static final int DEFAULT_PICKUP_DELAY = 100000;

    private final AudioEmitter audioEmitter;
    private final HitboxResolver hitboxResolver;
    @Nullable
    private ThrowDeploymentProperties properties;

    @Inject
    public ThrowDeployment(AudioEmitter audioEmitter, HitboxResolver hitboxResolver) {
        this.audioEmitter = audioEmitter;
        this.hitboxResolver = hitboxResolver;
    }

    public void configureProperties(ThrowDeploymentProperties properties) {
        this.properties = properties;
    }

    public Optional<DeploymentContext> createContext(Deployer deployer, Entity deployerEntity) {
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

        HitboxProvider<StaticBoundingBox> hitboxProvider = hitboxResolver.resolveDeploymentObjectHitboxProvider();

        ThrowDeploymentObject deploymentObject = new ThrowDeploymentObject(item, hitboxProvider);
        deploymentObject.setCooldown(properties.cooldown());
        deploymentObject.setHealth(properties.health());
        deploymentObject.setResistances(properties.resistances());

        properties.projectileEffects().forEach(effect -> effect.onLaunch(deployerEntity, deploymentObject));

        audioEmitter.playSounds(properties.throwSounds(), deployLocation);

        deployer.setHeldItem(null);

        return Optional.of(new DeploymentContext(deployerEntity, deploymentObject, deployer, deploymentObject));
    }
}
