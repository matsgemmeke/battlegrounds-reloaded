package nl.matsgemmeke.battlegrounds.item.deployment;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface DeployedObject {

    @NotNull
    Location getLocation();

    @NotNull
    World getWorld();

    void remove();
}
