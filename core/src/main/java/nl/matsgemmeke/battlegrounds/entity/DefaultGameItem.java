package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

public class DefaultGameItem implements GameItem {

    @NotNull
    private Item itemEntity;

    public DefaultGameItem(@NotNull Item itemEntity) {
        this.itemEntity = itemEntity;
    }

    @NotNull
    public Item getEntity() {
        return itemEntity;
    }

    @NotNull
    public Location getLocation() {
        return itemEntity.getLocation();
    }

    public double damage(double damageAmount) {
        return 0;
    }
}
