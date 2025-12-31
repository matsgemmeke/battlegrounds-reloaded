package nl.matsgemmeke.battlegrounds.item.trigger.tracking;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class DeployerTriggerTarget implements TriggerTarget {

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }

    @Override
    public World getWorld() {
        return null;
    }
}
