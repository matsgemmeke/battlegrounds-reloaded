package nl.matsgemmeke.battlegrounds.item.deploy.prime;

import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An item that is still held by the holder. A held item is not a {@link DeploymentObject} as it has no physical object
 * to reference to. Instead, it is supposed to be a temporary source for an {@link ItemEffect} before an actual
 * {@link DeploymentObject} is deployed to replace the held item.
 */
public class HeldItem implements DeploymentObject {

    private static final double HAND_HEIGHT_OFFSET = 1.0;
    private static final long DEFAULT_COOLDOWN = 0L;

    @NotNull
    private final Deployer deployer;
    private double health;
    @NotNull
    private final Entity deployerEntity;
    @NotNull
    private final ItemStack itemStack;

    public HeldItem(@NotNull Deployer deployer, @NotNull Entity deployerEntity, @NotNull ItemStack itemStack) {
        this.deployer = deployer;
        this.deployerEntity = deployerEntity;
        this.itemStack = itemStack;
    }

    public long getCooldown() {
        return DEFAULT_COOLDOWN;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Nullable
    public Damage getLastDamage() {
        return null;
    }

    @NotNull
    public Location getLocation() {
        return deployerEntity.getLocation().add(0, HAND_HEIGHT_OFFSET, 0);
    }

    @NotNull
    public World getWorld() {
        return deployerEntity.getWorld();
    }

    public boolean exists() {
        return !deployerEntity.isDead();
    }

    public boolean isDeployed() {
        return false;
    }

    public double damage(@NotNull Damage damage) {
        // An item held by the deployer technically cannot be damaged, so always return 0 here
        return 0;
    }

    public boolean isImmuneTo(@NotNull DamageType damageType) {
        return false;
    }

    public boolean matchesEntity(@NotNull Entity entity) {
        // An item held by the deployer technically cannot be damaged, so this always returns false
        return false;
    }

    public void remove() {
        deployer.removeItem(itemStack);
    }
}
