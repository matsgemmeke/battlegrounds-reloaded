package nl.matsgemmeke.battlegrounds.item.deploy.action;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.item.actor.ItemActor;
import nl.matsgemmeke.battlegrounds.item.deploy.*;
import nl.matsgemmeke.battlegrounds.item.deploy.object.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.object.ItemDeploymentObject;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A deployment system that produces a {@link DeploymentObject} using an {@link Item}.
 */
public class ThrowDeploymentAction implements DeploymentAction {

    // Take a high number to make sure the item cannot be picked up before the deployment is complete
    private static final int DEFAULT_PICKUP_DELAY = 100000;

    private final HitboxResolver hitboxResolver;
    private final SoundEmitter soundEmitter;
    @Nullable
    private ThrowDeploymentProperties properties;

    @Inject
    public ThrowDeploymentAction(HitboxResolver hitboxResolver, SoundEmitter soundEmitter) {
        this.hitboxResolver = hitboxResolver;
        this.soundEmitter = soundEmitter;
    }

    public void configureProperties(ThrowDeploymentProperties properties) {
        this.properties = properties;
    }

    @Override
    public Optional<DeploymentResult> perform(DeploymentContext context) {
        if (properties == null) {
            throw new IllegalStateException("Cannot perform deployment without properties configured");
        }

        Deployer deployer = context.deployer();
        Location deployLocation = deployer.getDeployLocation();
        Vector velocity = deployLocation.getDirection().multiply(properties.velocity());
        World world = deployer.getWorld();
        ItemStack itemStack = properties.itemTemplate().createItemStack();

        Item item = world.dropItem(deployLocation, itemStack);
        item.setPickupDelay(DEFAULT_PICKUP_DELAY);
        item.setVelocity(velocity);

        EntityKey entityKey = EntityKey.custom(context.itemName());
        HitboxProvider<StaticBoundingBox> hitboxProvider = hitboxResolver.resolveDeploymentObjectHitboxProvider();
        DestructionListener destructionListener = context.destructionListener();

        ItemDeploymentObject deploymentObject = new ItemDeploymentObject(item, entityKey, hitboxProvider, destructionListener);
        deploymentObject.setHealth(properties.health());

        properties.resistances().forEach(deploymentObject::addResistance);

        ItemActor actor = new ItemActor(item);
        long cooldown = properties.cooldown();

        properties.projectileEffects().forEach(effect -> effect.onLaunch(deployer, deploymentObject));

        soundEmitter.playSoundSequence(properties.soundSequence(), deployer::getDeployLocation);

        deployer.setHeldItem(null);

        return Optional.of(new DeploymentResult(deployer, deploymentObject, actor, cooldown));
    }
}
