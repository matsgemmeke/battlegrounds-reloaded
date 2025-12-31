package nl.matsgemmeke.battlegrounds.item.trigger.tracking;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

/**
 * A trigger target based on an item entity.
 */
public class ItemTriggerTarget implements TriggerTarget {

    private final Item item;

    public ItemTriggerTarget(Item item) {
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
