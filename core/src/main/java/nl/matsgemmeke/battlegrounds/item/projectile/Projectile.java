package nl.matsgemmeke.battlegrounds.item.projectile;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Projectile {

    boolean exists();

    @NotNull
    Location getLocation();

    @NotNull
    Vector getVelocity();

    void setVelocity(@NotNull Vector velocity);

    @NotNull
    World getWorld();

    boolean hasGravity();

    void setGravity(boolean gravity);
}
