package nl.matsgemmeke.battlegrounds.item.deploy.action;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.StaticBoundingBox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.item.actor.ItemActor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import nl.matsgemmeke.battlegrounds.item.deploy.object.ItemDeploymentObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DropDeploymentAction implements DeploymentAction {

    // Take a high number to make sure the item cannot be picked up before the deployment is complete
    private static final int DEFAULT_PICKUP_DELAY = 100000;
    private static final long COOLDOWN = 0L;

    private final HitboxResolver hitboxResolver;
    @Nullable
    private DropDeploymentProperties properties;

    @Inject
    public DropDeploymentAction(HitboxResolver hitboxResolver) {
        this.hitboxResolver = hitboxResolver;
    }

    public void configureProperties(DropDeploymentProperties properties) {
        this.properties = properties;
    }

    @Override
    public Optional<DeploymentResult> perform(Deployer deployer, DestructionListener destructionListener) {
        if (properties == null) {
            throw new IllegalStateException("Cannot perform deployment drop action without properties configured");
        }

        Location deployLocation = deployer.getDeployLocation();
        Vector velocity = deployer.getDeployLocation().getDirection().multiply(properties.velocity());
        ItemStack itemStack = properties.itemTemplate().createItemStack();
        World world = deployer.getWorld();

        Item item = world.dropItem(deployLocation, itemStack);
        item.setPickupDelay(DEFAULT_PICKUP_DELAY);
        item.setVelocity(velocity);

        HitboxProvider<StaticBoundingBox> hitboxProvider = hitboxResolver.resolveDeploymentObjectHitboxProvider();

        ItemDeploymentObject deploymentObject = new ItemDeploymentObject(item, hitboxProvider, destructionListener);
        deploymentObject.setHealth(properties.health());
        properties.resistances().forEach(deploymentObject::addResistance);

        ItemActor actor = new ItemActor(item);

        deployer.setHeldItem(null);

        return Optional.of(new DeploymentResult(deployer, deploymentObject, actor, COOLDOWN));
    }
}
