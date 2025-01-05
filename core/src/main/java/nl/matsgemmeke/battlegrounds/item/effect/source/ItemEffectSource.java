package nl.matsgemmeke.battlegrounds.item.effect.source;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface ItemEffectSource {

    boolean exists();

    @NotNull
    Location getLocation();

    @NotNull
    World getWorld();

    boolean isDeployed();

    void remove();
}
