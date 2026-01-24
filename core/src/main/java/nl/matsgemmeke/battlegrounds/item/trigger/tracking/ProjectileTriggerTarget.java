package nl.matsgemmeke.battlegrounds.item.trigger.tracking;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class ProjectileTriggerTarget implements TriggerTarget {

    private final Projectile projectile;

    public ProjectileTriggerTarget(Projectile projectile) {
        this.projectile = projectile;
    }

    @Override
    public boolean exists() {
        return projectile.isValid();
    }

    @Override
    public Location getLocation() {
        return projectile.getLocation();
    }

    @Override
    public Vector getVelocity() {
        return projectile.getVelocity();
    }

    @Override
    public World getWorld() {
        return projectile.getWorld();
    }
}
