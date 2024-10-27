package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HeldItem implements ActivationSource {

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
        return holder.getLocation();
    }

    @NotNull
    public World getWorld() {
        return holder.getWorld();
    }

    public void remove() {
        holder.removeItem(itemStack);
    }
}
