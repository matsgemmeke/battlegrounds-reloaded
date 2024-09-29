package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface Deployable {

    boolean exists();

    @NotNull
    Location getLocation();

    @NotNull
    World getWorld();

    void remove();
}
