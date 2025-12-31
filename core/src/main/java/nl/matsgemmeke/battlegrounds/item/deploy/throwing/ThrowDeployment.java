package nl.matsgemmeke.battlegrounds.item.deploy.throwing;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.ItemTriggerTarget;
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

    @Override
    public Optional<DeploymentResult> perform(Deployer deployer, Entity deployerEntity, DestructionListener destructionListener) {
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

        ThrowDeploymentObject deploymentObject = new ThrowDeploymentObject(item, hitboxProvider, destructionListener);
        deploymentObject.setHealth(properties.health());
        deploymentObject.setResistances(properties.resistances());

        ItemTriggerTarget triggerTarget = new ItemTriggerTarget(item);
        long cooldown = properties.cooldown();

        properties.projectileEffects().forEach(effect -> effect.onLaunch(deployer, deploymentObject));

        audioEmitter.playSounds(properties.throwSounds(), deployLocation);

        deployer.setHeldItem(null);

        return Optional.of(new DeploymentResult(deployer, deploymentObject, triggerTarget, cooldown));
    }
}
