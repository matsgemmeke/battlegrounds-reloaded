package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface ActivationSource {

    boolean exists();

    @NotNull
    Location getLocation();

    @NotNull
    World getWorld();

    void remove();
}
