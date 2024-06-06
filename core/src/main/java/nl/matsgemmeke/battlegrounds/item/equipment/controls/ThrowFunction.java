package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ThrowFunction implements ItemFunction<EquipmentHolder> {

    // Take a high number to make sure the item cannot be picked up before its ignition
    private static final int DEFAULT_PICKUP_DELAY = 1000;

    private double velocity;
    @NotNull
    private ItemStack itemStack;

    public ThrowFunction(@NotNull ItemStack itemStack, double velocity) {
        this.itemStack = itemStack;
        this.velocity = velocity;
    }

    public boolean isAvailable() {
        return false;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isPerforming() {
        return false;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull EquipmentHolder holder) {
        World world = holder.getEntity().getWorld();

        Item item = world.dropItem(holder.getEntity().getLocation(), itemStack);
        item.setPickupDelay(DEFAULT_PICKUP_DELAY);
        item.setVelocity(holder.getThrowingDirection().getDirection().multiply(velocity));

        return true;
    }
}
