package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public class ItemActor implements Actor {

    private final Item item;

    public ItemActor(Item item) {
        this.item = item;
    }

    @Override
    public boolean exists() {
        return item.isValid();
    }

    @Override
    public Location getLocation() {
        return item.getLocation();
    }

    @Override
    public Vector getVelocity() {
        return item.getVelocity();
    }

    @Override
    public World getWorld() {
        return item.getWorld();
    }
}
