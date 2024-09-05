package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DroppedItem implements Deployable {

    @NotNull
    private Item itemEntity;

    public DroppedItem(@NotNull Item itemEntity) {
        this.itemEntity = itemEntity;
    }

    @Nullable
    public Entity getDamageSource() {
        return itemEntity;
    }

    @NotNull
    public Location getLocation() {
        return itemEntity.getLocation();
    }

    @NotNull
    public World getWorld() {
        return itemEntity.getWorld();
    }

    public void remove() {
        itemEntity.remove();
    }
}
