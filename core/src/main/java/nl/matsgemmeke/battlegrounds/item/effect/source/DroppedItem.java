package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

/**
 * A deployed item in the form as a dropped {@link Item} entity.
 */
public class DroppedItem implements EffectSource {

    @NotNull
    private Item itemEntity;

    public DroppedItem(@NotNull Item itemEntity) {
        this.itemEntity = itemEntity;
    }

    public boolean exists() {
        return !itemEntity.isDead();
    }

    @NotNull
    public Location getLocation() {
        return itemEntity.getLocation();
    }

    @NotNull
    public World getWorld() {
        return itemEntity.getWorld();
    }

    public boolean isDeployed() {
        return true;
    }

    public void remove() {
        itemEntity.remove();
    }
}
