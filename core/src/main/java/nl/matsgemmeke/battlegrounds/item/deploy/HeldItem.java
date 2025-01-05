package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An item that is still held by the holder. A held item is not a {@link DeploymentObject} as it has no physical object
 * to reference to. Instead, it is supposed to be a temporary source for an {@link ItemEffect} before an actual
 * {@link DeploymentObject} is deployed to replace the held item.
 */
public class HeldItem implements EffectSource {

    private static final double HAND_HEIGHT_OFFSET = 1.0;

    @NotNull
    private ItemHolder holder;
    @NotNull
    private ItemStack itemStack;

    public HeldItem(@NotNull ItemHolder holder, @NotNull ItemStack itemStack) {
        this.holder = holder;
        this.itemStack = itemStack;
    }

    public boolean exists() {
        return !holder.getEntity().isDead();
    }

    @NotNull
    public Location getLocation() {
        return holder.getLocation().add(0, HAND_HEIGHT_OFFSET, 0);
    }

    @NotNull
    public World getWorld() {
        return holder.getWorld();
    }

    public boolean isDeployed() {
        return false;
    }

    public void remove() {
        holder.removeItem(itemStack);
    }
}
