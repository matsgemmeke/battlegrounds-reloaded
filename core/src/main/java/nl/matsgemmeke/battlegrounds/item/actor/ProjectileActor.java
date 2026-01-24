package nl.matsgemmeke.battlegrounds.item.actor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class ProjectileActor implements Actor {

    private final Projectile projectile;

    public ProjectileActor(Projectile projectile) {
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
