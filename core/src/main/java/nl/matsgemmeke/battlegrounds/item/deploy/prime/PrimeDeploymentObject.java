package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an item that is primed but held by the deployer and not yet deployed.
 */
public class PrimeDeploymentObject implements DeploymentObject, ItemEffectSource {

    private static final double HAND_HEIGHT_OFFSET = 1.0;

    private final Deployer deployer;
    private final Entity deployerEntity;
    private final ItemStack itemStack;
    private final UUID uniqueId;

    public PrimeDeploymentObject(Deployer deployer, Entity deployerEntity, ItemStack itemStack) {
        this.deployer = deployer;
        this.deployerEntity = deployerEntity;
        this.itemStack = itemStack;
        this.uniqueId = UUID.randomUUID();
    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public void setHealth(double health) {

    }

    @Nullable
    @Override
    public Damage getLastDamage() {
        return null;
    }

    @NotNull
    public Location getLocation() {
        return deployerEntity.getLocation().add(0, HAND_HEIGHT_OFFSET, 0);
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    public Vector getVelocity() {
        return deployerEntity.getVelocity();
    }

    @NotNull
    public World getWorld() {
        return deployerEntity.getWorld();
    }

    @Override
    public double damage(Damage damage) {
        return 0;
    }

    public boolean exists() {
        return !deployerEntity.isDead();
    }

    @Override
    public Hitbox getHitbox() {
        return null;
    }

    @Override
    public boolean isImmuneTo(@NotNull DamageType damageType) {
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
